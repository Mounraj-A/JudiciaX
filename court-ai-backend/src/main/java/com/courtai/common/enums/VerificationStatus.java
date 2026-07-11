package com.courtai.common.enums;

/**
 * Tracks admin verification status for Advocate and Clerk accounts.
 *
 * <p>After a user uploads their identity documents, an Admin reviews
 * and moves the status to {@code APPROVED} or {@code REJECTED}.</p>
 */
public enum VerificationStatus {

    /** Awaiting admin review. */
    PENDING,

    /** Admin approved — account transitions to ACTIVE. */
    APPROVED,

    /** Admin rejected — account transitions to SUSPENDED. */
    REJECTED
}
