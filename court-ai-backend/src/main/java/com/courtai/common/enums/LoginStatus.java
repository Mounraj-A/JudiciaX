package com.courtai.common.enums;

/**
 * Records the outcome of a login attempt in {@link com.courtai.auth.entity.LoginHistory}.
 */
public enum LoginStatus {

    /** Credentials valid, account active — login permitted. */
    SUCCESS,

    /** Wrong password or email not found. */
    FAILED,

    /** Account is locked — login blocked. */
    LOCKED,

    /** IP blocked due to rate-limit or brute-force detection. */
    BLOCKED
}
