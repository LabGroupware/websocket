package org.cresplanex.nova.websocket.service;

import org.cresplanex.nova.websocket.template.KeyValueTemplate;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private KeyValueTemplate keyValueTemplate;

    public void saveSession(String sessionId, String username) {
        keyValueTemplate.setValue(sessionId, username);
    }

    public String getUsername(String sessionId) {
        return (String) keyValueTemplate.getValue(sessionId).orElse(null);
    }

    public void deleteSession(String sessionId) {
        keyValueTemplate.delete(sessionId);
    }
}

