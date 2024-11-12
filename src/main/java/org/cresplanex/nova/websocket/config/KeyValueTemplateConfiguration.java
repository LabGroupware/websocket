package org.cresplanex.nova.websocket.config;

import org.cresplanex.nova.websocket.template.KeyValueTemplate;
import org.cresplanex.nova.websocket.template.RedisKeyValueTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class KeyValueTemplateConfiguration {

    @Bean
    public KeyValueTemplate keyValueTemplate(
            RedisTemplate<String, Object> redisTemplate
    ) {
        return new RedisKeyValueTemplate(redisTemplate);
    }
}
