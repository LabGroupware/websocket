package org.cresplanex.nova.websocket.ws.send;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnsubscribeResponseMessage {
    public static final String TYPE = "unsubscribe";

    private String messageId;
    private String type;
    private boolean success;
}
