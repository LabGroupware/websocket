package org.cresplanex.nova.websocket.config;

import org.cresplanex.core.commands.producer.CoreCommandProducerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.WebSocketHandler;

@Configuration
@Import({
    // 実装
    CoreCommandProducerConfiguration.class
})
public class CommandProducerConfiguration implements WebSocketHandler {

    @Override
    public void afterConnectionEstablished(org.springframework.web.socket.WebSocketSession session) throws Exception {
        // 実装
    }

    @Override
    public void handleMessage(org.springframework.web.socket.WebSocketSession session, org.springframework.web.socket.WebSocketMessage<?> message) throws Exception {
        // 実装
    }

    @Override
    public void handleTransportError(org.springframework.web.socket.WebSocketSession session, Throwable exception) throws Exception {
        // 実装
    }

    @Override
    public void afterConnectionClosed(org.springframework.web.socket.WebSocketSession session, org.springframework.web.socket.CloseStatus closeStatus) throws Exception {
        // 実装
    }

    @Override
    public boolean supportsPartialMessages() {
        // 実装
        return false;
    }
}
