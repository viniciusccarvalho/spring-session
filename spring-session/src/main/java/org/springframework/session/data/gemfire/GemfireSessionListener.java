package org.springframework.session.data.gemfire;

import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.session.events.SessionDestroyedEvent;

/**
 * Created by vcarvalho on 3/6/15.
 */
public class GemfireSessionListener extends CacheListenerAdapter {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void afterDestroy(EntryEvent event) {
        publish(String.valueOf(event.getKey()));
    }

    @Override
    public void afterInvalidate(EntryEvent event) {
        publish(String.valueOf(event.getKey()));
    }

    private void publish(String sessionId) {
        eventPublisher.publishEvent(new SessionDestroyedEvent(this, sessionId));
    }
}
