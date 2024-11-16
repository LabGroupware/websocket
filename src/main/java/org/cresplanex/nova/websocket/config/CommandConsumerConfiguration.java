package org.cresplanex.nova.websocket.config;

import org.cresplanex.core.commands.consumer.CoreCommandConsumerConfiguration;
import org.cresplanex.core.messaging.consumer.decorator.ReplyExceptionHandleDecoratorConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    // 実装
    CoreCommandConsumerConfiguration.class,
    ReplyExceptionHandleDecoratorConfiguration.class
})
public class CommandConsumerConfiguration {
}
