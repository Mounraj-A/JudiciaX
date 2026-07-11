package com.courtai.common.enums;

/**
 * Represents the lifecycle status of a user account.
 *
 * <p>Status transitions:</p>
 * <pre>
 * Registration
 *   → PENDING_VERIFICATION  (email not verified)
 *   → ACTIVE                (verified; or admin-approved for Advocate/Clerk)
 *   → LOCKED                (brute-force protection, timed)
 *   → SUSPENDED             (admin action)
 *   → INACTIVE              (user self-deactivated)
 *   → SOFT_DELETED          (admin permanently soft-deleted)
 * </pre>
 */
public enum AccountStatus {

    /** Email verified and account approved — can log in. */
    ACTIVE,

    /** Self-deactivated by user — cannot log in. */
    INACTIVE,

    /** Locked due to multiple failed login attempts — timed unlock. */
    LOCKED,

    /** Suspended by admin — requires admin action to reactivate. */
    SUSPENDED,

    /** Registered but email not yet verified, or awaiting admin approval. */
    PENDING_VERIFICATION,

    /** Logically deleted — never physically removed from database. */
    SOFT_DELETED
}
