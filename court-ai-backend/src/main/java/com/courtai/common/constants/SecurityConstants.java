package com.courtai.common.constants;

/**
 * Security-related constants for JWT, CORS, and endpoint configuration.
 *
 * <p>All JWT header names, prefixes, and public endpoint patterns are defined here
 * to ensure consistency between {@link com.courtai.config.SecurityConfig} and
 * {@link com.courtai.security.jwt.JwtAuthenticationFilter}.</p>
 */
public final class SecurityConstants {

    private SecurityConstants() { /* utility class */ }

    // =========================================================
    //  JWT
    // =========================================================

    /** HTTP header used to carry the JWT token. */
    public static final String JWT_HEADER = "Authorization";

    /** Prefix for JWT Bearer token values. */
    public static final String BEARER_PREFIX = "Bearer ";

    /** JWT claim key for the roles list. */
    public static final String JWT_ROLES_CLAIM = "roles";

    /** JWT claim key for the session ID. */
    public static final String JWT_SESSION_CLAIM = "sessionId";

    // =========================================================
    //  PUBLIC ENDPOINTS (no authentication required)
    // =========================================================

    public static final String[] PUBLIC_URLS = {
            "/auth/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/actuator/health",
            "/actuator/info",
            "/error"
    };

    // =========================================================
    //  ADMIN-ONLY ENDPOINTS
    // =========================================================

    public static final String[] ADMIN_URLS = {
            "/admin/**"
    };

    // =========================================================
    //  RATE LIMIT TARGETS
    // =========================================================

    /** Endpoints subject to rate limiting. */
    public static final String[] RATE_LIMITED_URLS = {
            "/auth/login",
            "/auth/register",
            "/auth/forgot-password",
            "/auth/send-otp"
    };

    /** Max requests per minute per IP for rate-limited auth endpoints. */
    public static final int RATE_LIMIT_LOGIN_PER_MINUTE = 10;

    /** Max registration attempts per minute per IP. */
    public static final int RATE_LIMIT_REGISTER_PER_MINUTE = 5;

    /** Max forgot-password requests per minute per IP. */
    public static final int RATE_LIMIT_FORGOT_PASSWORD_PER_MINUTE = 3;

    // =========================================================
    //  HASHING
    // =========================================================

    /** Algorithm used to hash tokens before DB storage. */
    public static final String TOKEN_HASH_ALGORITHM = "SHA-256";
}
