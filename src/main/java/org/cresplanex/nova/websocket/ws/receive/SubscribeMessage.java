package org.cresplanex.nova.websocket.ws.receive;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscribeMessage {

    private String messageId;
    private String type;
    private DataType data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DataType {
        private String aggregateType;
        private List<String> aggregateIds;
        private List<String> eventTypes;
    }
}
