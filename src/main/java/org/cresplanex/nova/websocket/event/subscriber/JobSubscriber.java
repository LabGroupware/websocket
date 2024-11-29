package org.cresplanex.nova.websocket.event.subscriber;

import lombok.AllArgsConstructor;
import org.cresplanex.api.state.common.constants.ServiceType;
import org.cresplanex.api.state.common.event.EventAggregateType;
import org.cresplanex.api.state.common.event.subscriber.BaseSubscriber;
import org.cresplanex.core.events.subscriber.DomainEventDispatcher;
import org.cresplanex.core.events.subscriber.DomainEventDispatcherFactory;
import org.cresplanex.nova.websocket.event.handler.JobEventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class JobSubscriber extends BaseSubscriber {

    @Bean
    public DomainEventDispatcher domainEventDispatcher(
            JobEventHandler jobEventHandler, DomainEventDispatcherFactory domainEventDispatcherFactory) {;
        return domainEventDispatcherFactory.make(
                this.getDispatcherId(ServiceType.NOVA_WEB_SOCKET, EventAggregateType.JOB),
                jobEventHandler.domainEventHandlers()
        );
    }
}
