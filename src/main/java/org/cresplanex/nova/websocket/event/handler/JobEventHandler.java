package org.cresplanex.nova.websocket.event.handler;

import lombok.AllArgsConstructor;
import org.cresplanex.api.state.common.event.EventAggregateType;
import org.cresplanex.api.state.common.event.model.SuccessJobEvent;
import org.cresplanex.api.state.common.event.model.job.JobBegan;
import org.cresplanex.api.state.common.event.model.job.JobFailed;
import org.cresplanex.api.state.common.event.model.job.JobProcessed;
import org.cresplanex.api.state.common.event.model.job.JobSuccess;
import org.cresplanex.api.state.common.event.model.user.UserCreated;
import org.cresplanex.core.events.common.DomainEventEnvelope;
import org.cresplanex.core.events.subscriber.DomainEventHandlers;
import org.cresplanex.core.events.subscriber.DomainEventHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class JobEventHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
//    private final JobService jobService;

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
        // TODO: Implement this
    }

    private void handleJobProcessed(DomainEventEnvelope<JobProcessed> dee) {
        logger.trace("Handling JobProcessed event: {}", dee.getEvent());
        // TODO: Implement this
    }

    private void handleJobSuccess(DomainEventEnvelope<SuccessJobEvent> dee) {
        logger.trace("Handling JobSuccess event: {}", dee.getEvent());
        // TODO: Implement this
    }

    private void handleJobFailed(DomainEventEnvelope<JobFailed> dee) {
        logger.trace("Handling JobFailed event: {}", dee.getEvent());
        // TODO: Implement this
    }
}
