package com.courtai.auth.service;

/**
 * Service for password reset token lifecycle.
 */
public interface PasswordResetService {
    /** Generates a reset token for the given email and returns the raw token. */
    String generateResetToken(String email, String ipAddress);

    /** Validates token, enforces password policy, updates password, invalidates token. */
    void resetPassword(String rawToken, String newPassword, String confirmPassword);
}
