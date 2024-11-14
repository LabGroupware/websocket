package org.cresplanex.nova.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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

/**
 * WebSocket接続ハンドラ
 * textMessageのみを処理し, バイナリであればエラーを投げる
 */
@Slf4j
public class ConnectionHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 接続確立時の処理
     *
     * @param session WebSocketセッション
     * @throws Exception 例外
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket Connection Established: {}", session.getId());
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
        log.info("Received message: {}", message.getPayload());

        try {
            ReceptionMessage receptionMessage = objectMapper.readValue(message.getPayload(), ReceptionMessage.class);

            switch (receptionMessage.getType()) {
                case "subscribe":
                    try {
                        SubscribeMessage subscribeMessageData = objectMapper.readValue(message.getPayload(), SubscribeMessage.class);
                        log.info("Subscribe: {}", subscribeMessageData.getData().getAggregateIds());
                    } catch (Exception e) {
                        CannnotParseResponseMessage cannnotParseResponseMessage = CannnotParseResponseMessage.builder()
                                .type(CannnotParseResponseMessage.TYPE)
                                .messageId(receptionMessage.getMessageId())
                                .requestType("subscribe")
                                .success(false)
                                .build();
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(cannnotParseResponseMessage)));
                        return;
                    }
                    SubscribeResponseMessage subscribeResponseMessage = SubscribeResponseMessage.builder()
                            .subscriptionId("subscriptionId")
                            .messageId(receptionMessage.getMessageId())
                            .type(SubscribeResponseMessage.TYPE)
                            .success(true)
                            .build();
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(subscribeResponseMessage)));
                    break;
                case "unsubscribe":
                    try {
                        UnsubscribeMessage unsubscribeMessageData = objectMapper.readValue(message.getPayload(), UnsubscribeMessage.class);
                        log.info("Unsubscribe: {}", unsubscribeMessageData);
                    }catch (Exception e) {
                        log.error("Cannot parse unsubscribe message: {}", e.getMessage());
                        log.error("Cannot parse payload: {}", message.getPayload());
                        CannnotParseResponseMessage cannnotParseResponseMessage = CannnotParseResponseMessage.builder()
                                .type(CannnotParseResponseMessage.TYPE)
                                .messageId(receptionMessage.getMessageId())
                                .requestType("unsubscribe")
                                .success(false)
                                .build();
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(cannnotParseResponseMessage)));
                        return;
                    }
                    UnsubscribeResponseMessage unsubscribeResponseMessage = UnsubscribeResponseMessage.builder()
                            .messageId(receptionMessage.getMessageId())
                            .type(UnsubscribeResponseMessage.TYPE)
                            .success(true)
                            .build();
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(unsubscribeResponseMessage)));
                    break;
                default:
                    UnsupportedResponseMessage unsupportedResponseMessage = UnsupportedResponseMessage.builder()
                            .messageId(receptionMessage.getMessageId())
                            .type(UnsupportedResponseMessage.TYPE)
                            .success(false)
                            .build();
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(unsupportedResponseMessage)));
                    break;
            }
        } catch (Exception e) {
            CannnotParseResponseMessage cannnotParseResponseMessage = CannnotParseResponseMessage.builder()
                    .type(CannnotParseResponseMessage.TYPE)
                    .success(false)
                    .build();
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(cannnotParseResponseMessage)));
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
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket Transport Error: {}", exception.getMessage());
    }

    /**
     * 接続終了時の処理
     *
     * @param session WebSocketセッション
     * @param closeStatus クローズステータス
     * @throws Exception 例外
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("WebSocket Connection Closed: {}", session.getId());
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
