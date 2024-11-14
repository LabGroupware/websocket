package org.cresplanex.nova.websocket.ws.send;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CannnotParseResponseMessage {

    public static final String TYPE = "cannot-parse";

    private String messageId;
    private String type;
    private String requestType;
    private boolean success;
}
