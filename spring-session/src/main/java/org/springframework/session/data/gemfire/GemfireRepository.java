package org.springframework.session.data.gemfire;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Vinicius Carvalho
 * @since 1.1
 */
public interface GemfireRepository extends CrudRepository<GemfireSessionRepository.GemfireSession, String> {

}
