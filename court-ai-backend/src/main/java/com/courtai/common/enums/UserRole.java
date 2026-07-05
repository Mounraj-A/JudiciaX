package com.courtai.common.enums;

/**
 * Enumeration of all user roles within the Judicial Case Management System.
 *
 * <p>Roles are prefixed with {@code ROLE_} to align with Spring Security conventions.</p>
 *
 * <ul>
 *   <li>{@link #ROLE_ADMIN}    — System administrator with full access</li>
 *   <li>{@link #ROLE_JUDGE}    — Judicial authority managing case hearings and verdicts</li>
 *   <li>{@link #ROLE_CLERK}    — Court clerk handling case filing and document management</li>
 *   <li>{@link #ROLE_ADVOCATE} — Legal representative (lawyer) for case parties</li>
 * </ul>
 */
public enum UserRole {

    ROLE_ADMIN,
    ROLE_JUDGE,
    ROLE_CLERK,
    ROLE_ADVOCATE
}
