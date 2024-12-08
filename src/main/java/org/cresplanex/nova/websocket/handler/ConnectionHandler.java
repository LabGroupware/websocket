package org.cresplanex.nova.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cresplanex.api.state.common.utils.CustomIdGenerator;
import org.cresplanex.nova.websocket.constants.WebSocketSetting;
import org.cresplanex.nova.websocket.template.KeyValueTemplate;
import org.cresplanex.nova.websocket.ws.WebSocketSessionManager;
import org.cresplanex.nova.websocket.ws.receive.ReceptionMessage;
import org.cresplanex.nova.websocket.ws.receive.SubscribeMessage;
import org.cresplanex.nova.websocket.ws.receive.UnsubscribeMessage;
import org.cresplanex.nova.websocket.ws.send.CannnotParseResponseMessage;
import org.cresplanex.nova.websocket.ws.send.SubscribeResponseMessage;
import org.cresplanex.nova.websocket.ws.send.UnsubscribeResponseMessage;
import org.cresplanex.nova.websocket.ws.send.UnsupportedResponseMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Set;

import static org.cresplanex.nova.websocket.constants.WebSocketSetting.*;

/**
 * WebSocket接続ハンドラ
 * textMessageのみを処理し, バイナリであればエラーを投げる
 */
@Slf4j
@RequiredArgsConstructor
public class ConnectionHandler extends TextWebSocketHandler {

    private final KeyValueTemplate keyValueTemplate;

    private final WebSocketSessionManager sessionManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 接続確立時の処理
     *
     * @param session WebSocketセッション
     * @throws Exception 例外
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = (String) session.getAttributes().get(WebSocketSetting.USER_ID_SESSION_ATTRIBUTE);
        if (userId == null) {
            log.error("User ID is not found in session attributes");
            return;
        }
        String socketId = session.getId();
        log.trace("WebSocket Connection Established: {}", socketId);

        keyValueTemplate.addSetValue(USER_KEY_PREFIX + userId, socketId);

        sessionManager.addSession(socketId, session);
    }

    /**
     * メッセージ受信時の処理
     *
     * @param session WebSocketセッション
     * @param message 受信メッセージ
     * @throws Exception 例外
     */
    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        String userId = (String) session.getAttributes().get(WebSocketSetting.USER_ID_SESSION_ATTRIBUTE);
        if (userId == null) {
            log.error("User ID is not found in session attributes");
            return;
        }
        String socketId = session.getId();

        log.trace("Received Message: {}\nFrom: {}", message.getPayload(), userId);

        try {
            ReceptionMessage receptionMessage = objectMapper.readValue(message.getPayload(), ReceptionMessage.class);

            switch (receptionMessage.getType()) {
                case "subscribe":
                    SubscribeMessage subscribeMessageData;
                    try {
                        subscribeMessageData = objectMapper.readValue(message.getPayload(), SubscribeMessage.class);
                        log.trace("Subscribe: {}", subscribeMessageData.getData().getAggregateIds());
                    } catch (Exception e) {
                        CannnotParseResponseMessage cannnotParseResponseMessage = CannnotParseResponseMessage.builder()
                                .type(CannnotParseResponseMessage.TYPE)
                                .messageId(receptionMessage.getMessageId())
                                .requestType("subscribe")
                                .success(false)
                                .build();

                        synchronized (session) {
                            if (session.isOpen()) {
                                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(cannnotParseResponseMessage)));
                            } else {
                                sessionManager.removeSession(socketId);
                            }
                        }
                        return;
                    }

                    CustomIdGenerator customIdGenerator = new CustomIdGenerator();
                    String subscriptionId = customIdGenerator.generate();

                    for (String aggregateId : subscribeMessageData.getData().getAggregateIds()) {
                        for (String eventType : subscribeMessageData.getData().getEventTypes()) {
                            String resource = subscribeMessageData.getData().getAggregateType() + ":" + aggregateId + ":" + eventType;
                            keyValueTemplate.addSetValue(RESOURCE_KEY_PREFIX + resource, subscriptionId);
                            keyValueTemplate.addSetValue(SUBSCRIPTION_TO_RESOURCE_KEY_PREFIX + subscriptionId, resource);
                        }
                    }

                    keyValueTemplate.addSetValue(SOCKET_KEY_PREFIX + socketId, subscriptionId);
                    keyValueTemplate.setValue(SUBSCRIPTION_TO_SOCKET_KEY_PREFIX + subscriptionId, socketId);

                    SubscribeResponseMessage subscribeResponseMessage = SubscribeResponseMessage.builder()
                            .subscriptionId(subscriptionId)
                            .messageId(receptionMessage.getMessageId())
                            .type(SubscribeResponseMessage.TYPE)
                            .success(true)
                            .build();

                    synchronized (session) {
                        if (session.isOpen()) {
                            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(subscribeResponseMessage)));
                        } else {
                            sessionManager.removeSession(socketId);
                        }
                    }
                    break;
                case "unsubscribe":
                    UnsubscribeMessage unsubscribeMessageData;
                    try {
                        unsubscribeMessageData = objectMapper.readValue(message.getPayload(), UnsubscribeMessage.class);
                        log.trace("Unsubscribe: {}", unsubscribeMessageData);
                    }catch (Exception e) {
                        log.error("Cannot parse unsubscribe message: {}", e.getMessage());
                        CannnotParseResponseMessage cannnotParseResponseMessage = CannnotParseResponseMessage.builder()
                                .type(CannnotParseResponseMessage.TYPE)
                                .messageId(receptionMessage.getMessageId())
                                .requestType("unsubscribe")
                                .success(false)
                                .build();

                        synchronized (session) {
                            if (session.isOpen()) {
                                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(cannnotParseResponseMessage)));
                            } else {
                                sessionManager.removeSession(socketId);
                            }
                        }
                        return;
                    }
                    List<String> subscriptionIds = unsubscribeMessageData.getData().getSubscriptionIds();

                    for (String subscriberId : subscriptionIds) {
                        log.info("Unsubscribe: {}", subscriberId);
                        Set<Object> resources = keyValueTemplate.getSetValues(SUBSCRIPTION_TO_RESOURCE_KEY_PREFIX + subscriberId);

                        log.info("Unsubscribing resources: {}", resources);

                        resources.forEach(resource -> {
                            keyValueTemplate.removeSetValues(RESOURCE_KEY_PREFIX + resource, subscriberId);
                        });
                        keyValueTemplate.delete(SUBSCRIPTION_TO_RESOURCE_KEY_PREFIX + subscriberId);
                    }

                    keyValueTemplate.removeSetValues(SOCKET_KEY_PREFIX + socketId, subscriptionIds);
                    subscriptionIds.forEach(subId -> keyValueTemplate.delete(SUBSCRIPTION_TO_SOCKET_KEY_PREFIX + subId));

                    UnsubscribeResponseMessage unsubscribeResponseMessage = UnsubscribeResponseMessage.builder()
                            .messageId(receptionMessage.getMessageId())
                            .type(UnsubscribeResponseMessage.TYPE)
                            .success(true)
                            .build();

                    synchronized (session) {
                        if (session.isOpen()) {
                            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(unsubscribeResponseMessage)));
                        } else {
                            sessionManager.removeSession(socketId);
                        }
                    }
                    break;
                default:
                    UnsupportedResponseMessage unsupportedResponseMessage = UnsupportedResponseMessage.builder()
                            .messageId(receptionMessage.getMessageId())
                            .type(UnsupportedResponseMessage.TYPE)
                            .success(false)
                            .build();

                    synchronized (session) {
                        if (session.isOpen()) {
                            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(unsupportedResponseMessage)));
                        } else {
                            sessionManager.removeSession(socketId);
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            CannnotParseResponseMessage cannnotParseResponseMessage = CannnotParseResponseMessage.builder()
                    .type(CannnotParseResponseMessage.TYPE)
                    .success(false)
                    .build();
            synchronized (session) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(cannnotParseResponseMessage)));
                } else {
                    sessionManager.removeSession(socketId);
                }
            }
        }
    }

    /**
     * 接続中エラー時の処理
     *
     * @param session WebSocketセッション
     * @param exception 例外
     * @throws Exception 例外
     */
    @Override
    public void handleTransportError(WebSocketSession session, @NonNull Throwable exception) throws Exception {
        log.trace("WebSocket Connection Closed from Client: {}", session.getId());

        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }

        clearSession(session);
    }

    /**
     * 接続終了時の処理
     *
     * @param session WebSocketセッション
     * @param closeStatus クローズステータス
     * @throws Exception 例外
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus closeStatus) throws Exception {
        log.trace("WebSocket Connection Closed: {}", session.getId());

        clearSession(session);
    }

    private void clearSession(WebSocketSession session) {
        String userId = (String) session.getAttributes().get(WebSocketSetting.USER_ID_SESSION_ATTRIBUTE);
        if (userId == null) {
            log.error("User ID is not found in session attributes");
            return;
        }
        String socketId = session.getId();

        Set<Object> subscriptionIds = keyValueTemplate.getSetValues(SOCKET_KEY_PREFIX + socketId);

        for (Object subscriptionId : subscriptionIds) {
            Set<Object> resources = keyValueTemplate.getSetValues(SUBSCRIPTION_TO_RESOURCE_KEY_PREFIX + subscriptionId);
            resources.forEach(resource -> {
                keyValueTemplate.removeSetValues(RESOURCE_KEY_PREFIX + resource, subscriptionId);
            });
            keyValueTemplate.delete(SUBSCRIPTION_TO_RESOURCE_KEY_PREFIX + subscriptionId);
        }

        keyValueTemplate.removeSetValues(USER_KEY_PREFIX + userId, socketId);
        keyValueTemplate.delete(SOCKET_KEY_PREFIX + socketId);
        subscriptionIds.forEach(subId -> keyValueTemplate.delete(SUBSCRIPTION_TO_SOCKET_KEY_PREFIX + subId));

        sessionManager.removeSession(socketId);
    }

    /**
     * 部分メッセージのサポート
     * 長いメッセージを分割して送信するかどうかを返します。
     *
     * @return 部分メッセージのサポート
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
