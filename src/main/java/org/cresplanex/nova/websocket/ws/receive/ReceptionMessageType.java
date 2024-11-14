package org.cresplanex.nova.websocket.ws.receive;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceptionMessageType<T> {

        private String messageId;
        private String type;
        private T data;
}
