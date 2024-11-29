package org.cresplanex.nova.websocket.template;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface KeyValueTemplate {
    void setValue(String key, Object value);
    void setValue(String key, Object value, long timeout, TimeUnit timeUnit);
    Long getExpire(String key, TimeUnit timeUnit);
    Optional<Object> getValue(String key);
    void delete(String key);
    boolean exists(String key);
    void addSetValue(String key, Object value);
    Set<Object> getSetValues(String key);
    void removeSetValues(String key, Object ...value);
}
