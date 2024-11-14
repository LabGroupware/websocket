package org.cresplanex.nova.websocket.ws.receive;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnsubscribeMessage {

    private String messageId;
    private String type;
    private DataType data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DataType {
        private List<String> subscriptionIds;
    }
}
