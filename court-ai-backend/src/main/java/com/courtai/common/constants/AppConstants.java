package com.courtai.common.constants;

/**
 * Global application-level constants shared across all modules.
 *
 * <p>Always reference these constants instead of hardcoding values in services,
 * controllers, or configuration classes.</p>
 */
public final class AppConstants {

    private AppConstants() { /* utility class */ }

    // =========================================================
    //  ACCOUNT SECURITY
    // =========================================================

    /** Maximum consecutive failed login attempts before account lock. */
    public static final int MAX_FAILED_ATTEMPTS = 5;

    /** Duration (in minutes) an account remains locked after brute-force detection. */
    public static final int ACCOUNT_LOCK_DURATION_MINUTES = 15;

    /** Maximum number of concurrent active sessions per user. */
    public static final int MAX_ACTIVE_SESSIONS = 5;

    // =========================================================
    //  OTP
    // =========================================================

    /** Number of digits in a mobile OTP. */
    public static final int OTP_LENGTH = 6;

    /** OTP validity window in minutes. */
    public static final int OTP_EXPIRY_MINUTES = 10;

    /** Maximum OTP verification attempts before locking OTP. */
    public static final int MAX_OTP_ATTEMPTS = 3;

    // =========================================================
    //  PASSWORD POLICY
    // =========================================================

    /** Minimum allowed password length. */
    public static final int PASSWORD_MIN_LENGTH = 8;

    /** Maximum allowed password length. */
    public static final int PASSWORD_MAX_LENGTH = 20;

    /** Number of previous passwords to check against for reuse. */
    public static final int PASSWORD_HISTORY_COUNT = 5;

    // =========================================================
    //  TOKENS
    // =========================================================

    /** Email verification token validity in hours. */
    public static final int EMAIL_VERIFICATION_TOKEN_EXPIRY_HOURS = 24;

    /** Password reset token validity in hours. */
    public static final int PASSWORD_RESET_TOKEN_EXPIRY_HOURS = 1;

    // =========================================================
    //  PAGINATION
    // =========================================================

    /** Default page number (0-indexed). */
    public static final int DEFAULT_PAGE = 0;

    /** Default page size for list endpoints. */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** Maximum allowed page size. */
    public static final int MAX_PAGE_SIZE = 100;

    // =========================================================
    //  SYSTEM
    // =========================================================

    /** System actor identifier used in audit logs for automated processes. */
    public static final String SYSTEM_ACTOR = "SYSTEM";

    /** Anonymous actor identifier for unauthenticated actions. */
    public static final String ANONYMOUS_ACTOR = "ANONYMOUS";

    /** Default language code. */
    public static final String DEFAULT_LANGUAGE = "en";

    /** Profile photo placeholder URL. */
    public static final String DEFAULT_AVATAR_URL = "/assets/avatar/default.png";
}
