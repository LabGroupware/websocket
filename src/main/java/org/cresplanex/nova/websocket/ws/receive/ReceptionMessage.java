package org.cresplanex.nova.websocket.ws.receive;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceptionMessage {

        private String messageId;
        private String type;
        private Object data;
}
