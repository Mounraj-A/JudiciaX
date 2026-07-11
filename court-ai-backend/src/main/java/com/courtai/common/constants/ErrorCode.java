package com.courtai.common.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Standardised error codes for every API error response.
 *
 * <p>Format: {@code MODULE_NNN — short message}</p>
 *
 * <p>Controllers and services throw exceptions; {@link com.courtai.exception.GlobalExceptionHandler}
 * maps each exception to the appropriate {@code ErrorCode} and includes it in the response body.</p>
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // =========================================================
    //  AUTH — 0xx
    // =========================================================

    AUTH_001("AUTH_001", "Invalid email or password"),
    AUTH_002("AUTH_002", "Account is locked due to multiple failed attempts"),
    AUTH_003("AUTH_003", "Invalid or malformed JWT token"),
    AUTH_004("AUTH_004", "JWT token has expired"),
    AUTH_005("AUTH_005", "Account is not yet verified"),
    AUTH_006("AUTH_006", "Email address already registered"),
    AUTH_007("AUTH_007", "Phone number already registered"),
    AUTH_008("AUTH_008", "Invalid OTP provided"),
    AUTH_009("AUTH_009", "OTP has expired"),
    AUTH_010("AUTH_010", "Account is pending admin approval"),
    AUTH_011("AUTH_011", "Refresh token is invalid or expired"),
    AUTH_012("AUTH_012", "Session has expired or been revoked"),
    AUTH_013("AUTH_013", "Too many requests — please try again later"),

    // =========================================================
    //  USER — 1xx
    // =========================================================

    USER_001("USER_001", "User not found"),
    USER_002("USER_002", "Password does not meet policy requirements"),
    USER_003("USER_003", "New password cannot be the same as current password"),
    USER_004("USER_004", "Password was used recently — choose a different one"),
    USER_005("USER_005", "Current password is incorrect"),
    USER_006("USER_006", "Session not found"),

    // =========================================================
    //  PERMISSION — 2xx
    // =========================================================

    PERM_001("PERM_001", "Access denied — insufficient permissions"),
    PERM_002("PERM_002", "Role not found"),
    PERM_003("PERM_003", "Permission not found"),

    // =========================================================
    //  VALIDATION — 3xx
    // =========================================================

    VAL_001("VAL_001", "Validation failed — check request body"),
    VAL_002("VAL_002", "Invalid email format"),
    VAL_003("VAL_003", "Invalid phone number format"),
    VAL_004("VAL_004", "Invalid token"),
    VAL_005("VAL_005", "Token has expired"),

    // =========================================================
    //  SYSTEM — 9xx
    // =========================================================

    SYS_001("SYS_001", "An unexpected internal error occurred");

    private final String code;
    private final String message;

    @Override
    public String toString() {
        return code + ": " + message;
    }
}
