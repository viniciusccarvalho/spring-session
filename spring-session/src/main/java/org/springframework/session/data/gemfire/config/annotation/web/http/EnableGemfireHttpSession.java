package org.springframework.session.data.gemfire.config.annotation.web.http;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.session.data.gemfire.GemfireRepository;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Created by vcarvalho on 3/6/15.
 */
@Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value={java.lang.annotation.ElementType.TYPE})


@Documented
@Configuration
@EnableGemfireRepositories(basePackageClasses=GemfireRepository.class)
@Import(GemfireHttpSessionConfiguration.class)
public @interface EnableGemfireHttpSession {
}
