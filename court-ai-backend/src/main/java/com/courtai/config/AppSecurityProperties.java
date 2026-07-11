package com.courtai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Typed binding for {@code app.security.*} properties in {@code application.yml}.
 *
 * <p>This class is the single source of truth for the security toggle.
 * It is injected into {@link SecurityConfig} to conditionally configure
 * the Spring Security filter chain without touching any auth classes.</p>
 *
 * <p><strong>Usage:</strong></p>
 * <pre>
 * # application.yml
 * app:
 *   security:
 *     enabled: false   # Development mode — no JWT required
 *     enabled: true    # Production mode — full JWT enforcement
 * </pre>
 */
@Component
@ConfigurationProperties(prefix = "app.security")
@Getter
@Setter
public class AppSecurityProperties {

    /**
     * Master switch for JWT authentication enforcement.
     *
     * <ul>
     *   <li>{@code false} — Development mode: all requests permitted, JWT filter bypassed.</li>
     *   <li>{@code true}  — Production mode: JWT authentication required on all protected routes.</li>
     * </ul>
     *
     * <p>Default: {@code true} (fail-safe — production behavior if property is absent).</p>
     */
    private boolean enabled = true;
}
