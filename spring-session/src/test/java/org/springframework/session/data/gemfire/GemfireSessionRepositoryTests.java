package org.springframework.session.data.gemfire;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.fest.assertions.Assertions.assertThat;

import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.session.ExpiringSession;
import org.springframework.session.Session;
import org.springframework.session.data.gemfire.GemfireSessionRepository.GemfireSession;

import java.util.concurrent.TimeUnit;

/**
 * Created by vcarvalho on 3/5/15.
 */

@RunWith(MockitoJUnitRunner.class)
public class GemfireSessionRepositoryTests {

    @Mock
    private GemfireRepository repository;

    private GemfireSessionRepository sessionRepository;

    @Before
    public void setup(){
        this.sessionRepository = new GemfireSessionRepository(repository);
    }

    @Test
    public void createSession() throws Exception{
        long now = System.currentTimeMillis();
        Thread.sleep(1);
        GemfireSession session = sessionRepository.createSession();
        assertThat(session.getLastAccessedTime()).isNotNull();
        assertThat(session.getId()).isNotNull();
        assertThat(session.getCreationTime()).isGreaterThan(now);
        long elapsed = System.currentTimeMillis();
        assertThat(session.getLastAccessedTime()).isLessThan(elapsed);
    }

    @Test
    public void saveNewSession() throws Exception {
        GemfireSession session = sessionRepository.createSession();
        long lastAccessed = session.getLastAccessedTime();
        Thread.sleep(1);
        sessionRepository.save(session);
        assertThat(session.getLastAccessedTime()).isGreaterThan(lastAccessed);
    }

    @Test
    public void setNewAttribute() throws Exception {
        GemfireSession session = sessionRepository.createSession();
        Mockito.when(repository.findOne(session.getId())).thenReturn(session);
        session.setAttribute("foo","bar");
        sessionRepository.save(session);
        assertThat(sessionRepository.getSession(session.getId()).getAttribute("foo")).isEqualTo("bar");
    }

    @Test
    public void expired() throws Exception {
        GemfireSession session = sessionRepository.createSession();
        session.setLastAccessedTime(System.currentTimeMillis()-(TimeUnit.SECONDS.toMillis(session.getMaxInactiveIntervalInSeconds()) +1));
        assertThat(session.isExpired()).isTrue();
    }

}
