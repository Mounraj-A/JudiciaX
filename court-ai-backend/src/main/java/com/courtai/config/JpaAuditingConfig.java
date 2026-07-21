package com.courtai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Dedicated JPA Auditing configuration.
 *
 * Keeping @EnableJpaAuditing on a separate @Configuration class
 * (rather than on the main @SpringBootApplication class) prevents Spring
 * DevTools from losing the auditorAwareImpl bean reference during hot-reload
 * cycles, which caused the application to fail to start.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class JpaAuditingConfig {
    // No beans needed here — AuditorAwareImpl is registered as a @Component.
}
