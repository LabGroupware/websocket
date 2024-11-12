package org.cresplanex.nova.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

/**
 * WebSocket接続ハンドラ
 * textMessageのみを処理し, バイナリであればエラーを投げる
 */
@Slf4j
public class ConnectionHandler extends TextWebSocketHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

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
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("Received message: {}", message.getPayload());

        try {
            // メッセージをパース
            var jsonNode = objectMapper.readTree(message.getPayload());
            var type = jsonNode.get("type").asText();
            var data = jsonNode.get("data");

            // メッセージタイプによって処理を分岐
            if (type.equals("ping")) {// pingメッセージの場合, pongメッセージを返す
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of("type", "pong"))));
            } else {// 未知のメッセージの場合, エラーメッセージを返す
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of("type", "error", "message", "Unknown message type"))));
            }
        } catch (Exception e) {
            // メッセージのパースに失敗した場合, エラーメッセージを返す
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of("type", "error", "message", "Failed to parse message"))));
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
