package org.cresplanex.nova.websocket.ws.send;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponseMessage {

    public static final String TYPE = "event";

    private String eventType;
    private Object data;
    private String type;
}
