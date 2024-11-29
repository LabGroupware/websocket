package org.cresplanex.nova.websocket.event.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.common.event.EventAggregateType;
import org.cresplanex.api.state.common.event.model.SuccessJobEvent;
import org.cresplanex.api.state.common.event.model.job.JobBegan;
import org.cresplanex.api.state.common.event.model.job.JobFailed;
import org.cresplanex.api.state.common.event.model.job.JobProcessed;
import org.cresplanex.api.state.common.event.model.job.JobSuccess;
import org.cresplanex.core.events.common.DomainEventEnvelope;
import org.cresplanex.core.events.subscriber.DomainEventHandlers;
import org.cresplanex.core.events.subscriber.DomainEventHandlersBuilder;
import org.cresplanex.nova.websocket.template.KeyValueTemplate;
import org.cresplanex.nova.websocket.ws.WebSocketSessionManager;
import org.cresplanex.nova.websocket.ws.send.EventResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.cresplanex.nova.websocket.constants.WebSocketSetting.RESOURCE_KEY_PREFIX;
import static org.cresplanex.nova.websocket.constants.WebSocketSetting.SUBSCRIPTION_TO_SOCKET_KEY_PREFIX;

@RequiredArgsConstructor
@Component
public class JobEventHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final KeyValueTemplate keyValueTemplate;
    private final WebSocketSessionManager sessionManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DomainEventHandlers domainEventHandlers() {
        return DomainEventHandlersBuilder
                .forAggregateType(EventAggregateType.JOB)
                .onEvent(JobBegan.class, this::handleJobBegan, JobBegan.TYPE)
                .onEvent(JobProcessed.class, this::handleJobProcessed, JobProcessed.TYPE)
                .onEvent(SuccessJobEvent.class, this::handleJobSuccess, JobSuccess.TYPE)
                .onEvent(JobFailed.class, this::handleJobFailed, JobFailed.TYPE)
                .build();
    }

    private void handleJobBegan(DomainEventEnvelope<JobBegan> dee) {
        logger.trace("Handling JobBegan event: {}", dee.getEvent());

        this.writeEventToSocket(dee.getEvent().getJobId(), EventTypes.JOB_BEGAN, dee.getEvent());
    }

    private void handleJobProcessed(DomainEventEnvelope<JobProcessed> dee) {
        logger.trace("Handling JobProcessed event: {}", dee.getEvent());

        this.writeEventToSocket(dee.getEvent().getJobId(), EventTypes.JOB_PROCESSED, dee.getEvent());
    }

    private void handleJobSuccess(DomainEventEnvelope<SuccessJobEvent> dee) {
        logger.trace("Handling JobSuccess event: {}", dee.getEvent());

        this.writeEventToSocket(dee.getEvent().getJobId(), EventTypes.JOB_SUCCESS, dee.getEvent());
    }

    private void handleJobFailed(DomainEventEnvelope<JobFailed> dee) {
        logger.trace("Handling JobFailed event: {}", dee.getEvent());

        this.writeEventToSocket(dee.getEvent().getJobId(), EventTypes.JOB_FAILED, dee.getEvent());
    }

    private void writeEventToSocket(String jobId, EventTypes eventType, Object event) {
        Set<String> subscriptionIds = getSubscriptionIds(jobId, eventType);

        Set<String> socketIds = new HashSet<>();

        subscriptionIds.forEach(subscriptionId -> {
            Optional<String> socketId = keyValueTemplate.getValue(SUBSCRIPTION_TO_SOCKET_KEY_PREFIX + subscriptionId)
                    .map(Object::toString);
            socketId.ifPresent(socketIds::add);
        });

        socketIds.forEach(socketId -> {
            if (sessionManager.containsSession(socketId)) {
                WebSocketSession session = sessionManager.getSession(socketId);
                if (session.isOpen()) {
                    try {
                        EventResponseMessage responseMessage = new EventResponseMessage(eventType.toString(), event);
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(responseMessage)));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    sessionManager.removeSession(socketId);
                }
            }
        });
    }

    private Set<String> getSubscriptionIds(String jobId, EventTypes eventType) {
        String resourceKey = RESOURCE_KEY_PREFIX + EventAggregateType.JOB + ":" + jobId + ":" + eventType.toString();
        Set<Object> raw = keyValueTemplate.getSetValues(resourceKey);

        return raw.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    enum EventTypes {
        JOB_BEGAN,
        JOB_PROCESSED,
        JOB_SUCCESS,
        JOB_FAILED
    }
}
