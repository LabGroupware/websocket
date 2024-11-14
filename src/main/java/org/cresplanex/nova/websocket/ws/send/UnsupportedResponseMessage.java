package org.cresplanex.nova.websocket.ws.send;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnsupportedResponseMessage {

    public static final String TYPE = "unsupported";

    private String messageId;
    private String type;
    private boolean success;
}
