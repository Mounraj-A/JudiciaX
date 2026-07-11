package com.courtai.common.enums;

/**
 * Tracks the state of a {@link com.courtai.auth.entity.UserSession}.
 */
public enum SessionStatus {

    /** Session is currently active. */
    ACTIVE,

    /** Session expired naturally (token TTL elapsed). */
    EXPIRED,

    /** User explicitly logged out. */
    LOGGED_OUT,

    /** Session revoked by user (from another device) or by admin. */
    REVOKED
}
