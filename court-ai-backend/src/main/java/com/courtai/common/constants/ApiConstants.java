package com.courtai.common.constants;

/**
 * API path and messaging constants shared across all REST controllers.
 */
public final class ApiConstants {

    private ApiConstants() { /* utility class */ }

    // =========================================================
    //  BASE PATHS
    // =========================================================

    public static final String API_AUTH_BASE   = "/auth";
    public static final String API_USER_BASE   = "/users";
    public static final String API_ADMIN_BASE  = "/admin";

    // =========================================================
    //  COMMON MESSAGES
    // =========================================================

    public static final String MSG_REGISTER_SUCCESS           = "Registration successful. Please verify your email.";
    public static final String MSG_LOGIN_SUCCESS              = "Login successful.";
    public static final String MSG_LOGOUT_SUCCESS             = "Logged out successfully.";
    public static final String MSG_TOKEN_REFRESHED            = "Token refreshed successfully.";
    public static final String MSG_FORGOT_PASSWORD_SENT       = "Password reset token generated. Check your email.";
    public static final String MSG_RESET_PASSWORD_SUCCESS     = "Password has been reset successfully.";
    public static final String MSG_CHANGE_PASSWORD_SUCCESS    = "Password changed successfully.";
    public static final String MSG_EMAIL_VERIFIED             = "Email verified successfully.";
    public static final String MSG_OTP_SENT                   = "OTP sent to your mobile number.";
    public static final String MSG_OTP_VERIFIED               = "Mobile number verified successfully.";
    public static final String MSG_PROFILE_RETRIEVED          = "Profile retrieved successfully.";
    public static final String MSG_PROFILE_UPDATED            = "Profile updated successfully.";
    public static final String MSG_SESSIONS_RETRIEVED         = "Sessions retrieved successfully.";
    public static final String MSG_SESSION_REVOKED            = "Session revoked successfully.";
    public static final String MSG_ALL_SESSIONS_REVOKED       = "All sessions revoked successfully.";
    public static final String MSG_USER_APPROVED              = "User account approved successfully.";
    public static final String MSG_USER_REJECTED              = "User account rejected.";
    public static final String MSG_USER_LOCKED                = "User account locked.";
    public static final String MSG_USER_UNLOCKED              = "User account unlocked.";
    public static final String MSG_USERS_RETRIEVED            = "Users retrieved successfully.";
    public static final String MSG_USER_RETRIEVED             = "User retrieved successfully.";
    public static final String MSG_USER_DELETED               = "User deleted successfully.";
    public static final String MSG_ROLE_ASSIGNED              = "Role assigned successfully.";
    public static final String MSG_DASHBOARD_RETRIEVED        = "Dashboard data retrieved.";
    public static final String MSG_PRIVACY_SETTINGS_UPDATED   = "Privacy settings updated.";
    public static final String MSG_RESEND_VERIFICATION_EMAIL  = "Verification email resent.";
}
