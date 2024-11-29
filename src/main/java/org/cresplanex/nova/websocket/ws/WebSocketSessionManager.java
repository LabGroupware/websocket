package org.cresplanex.nova.websocket.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebSocketSessionManager {

    private final Map<String, WebSocketSession> sessions = new HashMap<>();

    public void addSession(String socketId, WebSocketSession session) {
        sessions.put(socketId, session);
    }

    public void removeSession(String socketId) {
        sessions.remove(socketId);
    }

    public void removeAllSessions() {
        sessions.clear();
    }

    public boolean containsSession(String socketId) {
        return sessions.containsKey(socketId);
    }

    public WebSocketSession getSession(String socketId) {
        return sessions.get(socketId);
    }
}
