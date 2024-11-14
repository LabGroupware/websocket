package org.cresplanex.nova.websocket.ws.send;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscribeResponseMessage {
    public static final String TYPE = "subscribe";

    private String messageId;
    private String type;
    private String subscriptionId;
    private boolean success;
}
