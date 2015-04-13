package org.springframework.session.data.gemfire;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.session.events.SessionDestroyedEvent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by vcarvalho on 3/6/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class GemfireSessionRepositoryApplicationTests {
    @Autowired
    private SessionDestroyedEventRegistry registry;

    private final Object lock = new Object();

    @Before
    public void setup() {
        registry.setLock(lock);
    }


    @Test
    public void init(){}

    static class SessionDestroyedEventRegistry implements ApplicationListener<SessionDestroyedEvent> {
        private boolean receivedEvent;
        private Object lock;

        public void onApplicationEvent(SessionDestroyedEvent event) {
            receivedEvent = true;
            synchronized (lock) {
                lock.notifyAll();
            }
        }

        public boolean receivedEvent() {
            return receivedEvent;
        }

        public void setLock(Object lock) {
            this.lock = lock;
        }
    }

    @Configuration
    @EnableGemfireRepositories
    @ImportResource("classpath:/gemfire-cache-test.xml")
    static class Config {

    }

}
