package org.cresplanex.nova.websocket.config;

import org.cresplanex.nova.websocket.handler.ConnectionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.List;

@Configuration
@EnableWebSocket
public class ConnectionConfiguration implements WebSocketConfigurer {

    @Value("${app.front.origins}")
    private String frontOrigins;

    @Override
    public void registerWebSocketHandlers(org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry registry) {
        List<String> origins = List.of(frontOrigins.split(","));

        registry.addHandler(connectionHandler(), "/ws")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOriginPatterns("*");

//                .setAllowedOrigins(origins.toArray(new String[0]));
    }

    @Bean
    public WebSocketHandler connectionHandler() {
        return new ConnectionHandler();
    }
}
