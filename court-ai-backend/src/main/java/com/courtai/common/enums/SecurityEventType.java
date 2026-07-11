package com.courtai.common.enums;

/**
 * Types of security events logged to {@link com.courtai.auth.entity.SecurityEvent}.
 *
 * <p>All security-relevant activities — both successful and failed — are recorded
 * via {@link com.courtai.auth.service.SecurityEventService}.</p>
 */
public enum SecurityEventType {

    /** A login attempt with invalid credentials. */
    FAILED_LOGIN,

    /** A JWT token with an invalid signature was presented. */
    INVALID_JWT,

    /** An expired JWT was submitted. */
    TOKEN_EXPIRED,

    /** User logged in from a device/IP not previously seen. */
    NEW_DEVICE_LOGIN,

    /** User has concurrent active sessions above the configured maximum. */
    MULTI_DEVICE_LOGIN,

    /** Account locked after exceeding max failed attempts. */
    ACCOUNT_LOCKED,

    /** Account auto-unlocked after timed lock expired. */
    ACCOUNT_AUTO_UNLOCKED,

    /** User successfully changed their password. */
    PASSWORD_CHANGED,

    /** Admin user authenticated. */
    ADMIN_LOGIN,

    /** A refresh token was rotated (old invalidated, new issued). */
    TOKEN_REFRESH,

    /** All sessions for a user were revoked. */
    ALL_SESSIONS_REVOKED,

    /** A session was individually revoked. */
    SESSION_REVOKED,

    /** Rate limit exceeded for an IP address. */
    RATE_LIMIT_EXCEEDED,

    /** A password reset was requested. */
    PASSWORD_RESET_REQUESTED,

    /** A password reset was completed. */
    PASSWORD_RESET_COMPLETED
}
