package com.courtai.config;

import com.courtai.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Central Spring Security configuration for the Court AI Backend.
 *
 * <h3>Security Toggle</h3>
 * <p>This class supports a runtime security toggle controlled by the
 * {@code app.security.enabled} property in {@code application.yml}.</p>
 *
 * <ul>
 *   <li><b>Development mode</b> ({@code app.security.enabled: false}):
 *     <ul>
 *       <li>The {@link JwtAuthenticationFilter} is NOT added to the filter chain.</li>
 *       <li>All HTTP requests are permitted without a JWT token.</li>
 *       <li>Swagger UI, Actuator, and all API endpoints are freely accessible.</li>
 *       <li>CORS, response headers, and all other beans remain active.</li>
 *       <li>Zero auth classes are modified or disabled — only the chain changes.</li>
 *     </ul>
 *   </li>
 *   <li><b>Production mode</b> ({@code app.security.enabled: true}):
 *     <ul>
 *       <li>Full JWT authentication is enforced.</li>
 *       <li>{@link JwtAuthenticationFilter} is registered before {@code UsernamePasswordAuthenticationFilter}.</li>
 *       <li>Only {@link #PUBLIC_ENDPOINTS} are open; all others require authentication.</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p><b>How to switch:</b> Change {@code app.security.enabled} in {@code application.yml}.
 * No Java code modification is ever needed.</p>
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter    jwtAuthenticationFilter;
    private final UserDetailsService         userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler     jwtAccessDeniedHandler;

    /** Typed binding for {@code app.security.*} properties. */
    private final AppSecurityProperties      securityProperties;

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:4200,http://localhost:5173}")
    private List<String> allowedOrigins;

    @Value("${app.cors.allowed-methods:GET,POST,PUT,PATCH,DELETE,OPTIONS}")
    private List<String> allowedMethods;

    @Value("${app.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${app.cors.max-age:3600}")
    private long maxAge;

    /**
     * Public endpoints that are always accessible regardless of the security toggle.
     * In production mode these are open; in development mode ALL endpoints are open.
     */
    private static final String[] PUBLIC_ENDPOINTS = {
            "/auth/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/actuator/health",
            "/actuator/info",
            "/error"
    };

    /**
     * Builds the Spring Security filter chain.
     *
     * <p>Behaviour is determined at startup by {@code app.security.enabled}:</p>
     * <ul>
     *   <li>{@code false} → Development mode: permit all, skip JWT filter.</li>
     *   <li>{@code true}  → Production mode: JWT filter + endpoint authorization rules.</li>
     * </ul>
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        if (!securityProperties.isEnabled()) {
            // ══════════════════════════════════════════════════════════════
            //  DEVELOPMENT MODE — Security is DISABLED
            //  • JWT filter is NOT registered.
            //  • All endpoints are publicly accessible.
            //  • CORS and response headers remain active for accuracy.
            //  • Toggle back to production: app.security.enabled: true
            // ══════════════════════════════════════════════════════════════
            log.warn("╔══════════════════════════════════════════════════════╗");
            log.warn("║  ⚠  SECURITY IS DISABLED — DEVELOPMENT MODE ACTIVE  ║");
            log.warn("║  All API endpoints are publicly accessible.          ║");
            log.warn("║  Set app.security.enabled=true for production.       ║");
            log.warn("╚══════════════════════════════════════════════════════╝");

            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth
                            .anyRequest().permitAll())
                    .headers(headers -> headers
                            .frameOptions(frame -> frame.sameOrigin())   // relax for Swagger iframes
                            .xssProtection(xss -> xss.disable())
                    );
            // JWT filter intentionally NOT added in dev mode
            return http.build();
        }

        // ══════════════════════════════════════════════════════════════════
        //  PRODUCTION MODE — Full JWT authentication enforced
        //  • JwtAuthenticationFilter processes every request.
        //  • Only PUBLIC_ENDPOINTS bypass authentication.
        //  • All other requests require a valid Bearer token.
        // ══════════════════════════════════════════════════════════════════
        log.info("Security is ENABLED — JWT authentication is enforced.");

        http
                // ── Disable CSRF (stateless JWT — no session) ──────────────────────
                .csrf(AbstractHttpConfigurer::disable)

                // ── CORS ────────────────────────────────────────────────────────────
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ── Session management: stateless (JWT) ─────────────────────────────
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ── Exception handling ───────────────────────────────────────────────
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))

                // ── Authorization rules ──────────────────────────────────────────────
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated())

                // ── Security response headers ────────────────────────────────────────
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .xssProtection(xss -> xss.disable())
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives("default-src 'self'"))
                        .referrerPolicy(referrer ->
                                referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                )

                // ── Authentication provider ──────────────────────────────────────────
                .authenticationProvider(authenticationProvider())

                // ── JWT filter before UsernamePasswordAuthenticationFilter ───────────
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS configuration sourced from {@code application.yml}.
     * Active in both development and production modes.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.addAllowedHeader(allowedHeaders);
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * BCrypt password encoder with strength factor 12.
     * Always available as a bean regardless of security toggle.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * DAO authentication provider backed by {@link UserDetailsService} and BCrypt.
     * Always registered as a bean — used by auth endpoints in both modes.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Exposes the {@link AuthenticationManager} bean for use in auth services.
     * Always available — auth services remain functional in development mode.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
