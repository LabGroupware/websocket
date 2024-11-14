package org.cresplanex.nova.websocket.config;

import lombok.RequiredArgsConstructor;
import org.cresplanex.nova.websocket.auth.JwtHandshakeInterceptor;
import org.cresplanex.nova.websocket.handler.ConnectionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.List;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class ConnectionConfiguration implements WebSocketConfigurer {

    @Value("${app.front.origins}")
    private String frontOrigins;

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry registry) {
        List<String> origins = List.of(frontOrigins.split(","));

        registry.addHandler(connectionHandler(), "/ws")
                .setHandshakeHandler(new DefaultHandshakeHandler())
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOriginPatterns("*");

//                .setAllowedOrigins(origins.toArray(new String[0]));
    }

    @Bean
    public WebSocketHandler connectionHandler() {
        return new ConnectionHandler();
    }
}
