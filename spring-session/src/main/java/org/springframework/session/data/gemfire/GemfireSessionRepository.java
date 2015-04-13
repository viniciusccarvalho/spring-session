package org.springframework.session.data.gemfire;

import com.gemstone.gemfire.cache.CustomExpiry;
import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.ExpirationAction;
import com.gemstone.gemfire.cache.ExpirationAttributes;
import org.springframework.data.gemfire.mapping.Region;
import org.springframework.session.ExpiringSession;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.annotation.Id;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

/**
 * @author Vinicius Carvalho
 */
public class GemfireSessionRepository implements SessionRepository<GemfireSessionRepository.GemfireSession> {


    private GemfireRepository repository;

    public GemfireSessionRepository(GemfireRepository repository){
        this.repository = repository;
    }

    /**
     * If non-null, this value is used to override the default value for {@link RedisSession#setMaxInactiveIntervalInSeconds(int)}.
     */
    private Integer defaultMaxInactiveInterval;

    public GemfireSession createSession() {
        GemfireSession session = new GemfireSession();
        if(defaultMaxInactiveInterval != null){
            session.setMaxInactiveIntervalInSeconds(defaultMaxInactiveInterval);
        }
        repository.save(session);
        return session;
    }

    public void save(GemfireSession session) {
        session.setLastAccessedTime(System.currentTimeMillis());
        repository.save(session);
    }

    public GemfireSession getSession(String id) {
        GemfireSession session = repository.findOne(id);
        if(session == null){
            return null;
        }
        session.setLastAccessedTime(System.currentTimeMillis());
        return session;
    }

    public void delete(String id) {
        GemfireSession session = repository.findOne(id);
        if(session != null){
            repository.delete(session);
        }
    }


    public void setDefaultMaxInactiveInterval(int defaultMaxInactiveInterval) {
        this.defaultMaxInactiveInterval = defaultMaxInactiveInterval;
    }

    @Region("spring_session_sessions")
    final class GemfireSession implements ExpiringSession, Serializable, CustomExpiry {

        /**
         *
         */
        private static final long serialVersionUID = 6719898658776845883L;
        @Id
        private final String id = UUID.randomUUID().toString();
        public static final int DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS = 1800;

        private ExpirationAttributes CUSTOM_EXPIRY = new ExpirationAttributes(DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS, ExpirationAction.DESTROY);

        private long creationTime = System.currentTimeMillis();
        private long lastAccessedTime = creationTime;

        private Map<String, Object> attributes = new HashMap<String, Object>();
        private int maxInactiveInterval = DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS;

        public String getId() {
            return id;
        }

        public Object getAttribute(String attributeName) {
            return attributes.get(attributeName);
        }

        public Set<String> getAttributeNames() {
            return attributes.keySet();
        }

        public void setAttribute(String attributeName, Object attributeValue) {
            if (attributeValue == null) {
                removeAttribute(attributeName);
            } else {
                attributes.put(attributeName, attributeValue);
            }
        }

        public void removeAttribute(String attributeName) {
            attributes.remove(attributeName);
        }

        public long getCreationTime() {
            return creationTime;
        }

        public long getLastAccessedTime() {
            return lastAccessedTime;
        }

        public void setMaxInactiveIntervalInSeconds(int interval) {
            this.maxInactiveInterval = interval;
            this.CUSTOM_EXPIRY = new ExpirationAttributes(maxInactiveInterval,ExpirationAction.DESTROY);
        }

        public int getMaxInactiveIntervalInSeconds() {
            return maxInactiveInterval;
        }

        public boolean isExpired() {
            return isExpired(System.currentTimeMillis());
        }

        boolean isExpired(long now) {
            if (maxInactiveInterval < 0) {
                return false;
            }
            return now - TimeUnit.SECONDS.toMillis(maxInactiveInterval) >= lastAccessedTime;
        }

        public boolean equals(Object obj) {
            return obj instanceof Session && id.equals(((Session) obj).getId());
        }

        public int hashCode() {
            return id.hashCode();
        }

        public void setLastAccessedTime(long lastAccessedTime) {
            this.lastAccessedTime = lastAccessedTime;
        }

        public ExpirationAttributes getExpiry(com.gemstone.gemfire.cache.Region.Entry entry) {
            return CUSTOM_EXPIRY;
        }

        public void close() {

        }
    }


}
