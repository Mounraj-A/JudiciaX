package com.courtai.auth.service;

/**
 * Service for email verification workflow.
 *
 * <p>Since email sending is not yet configured, the raw token is returned
 * in the API response so it can be passed to /auth/verify-email.</p>
 */
public interface EmailVerificationService {

    /**
     * Generates an email verification token for the given user and returns the raw token.
     *
     * @param userUuid the UUID of the user
     * @return raw UUID token (caller simulates email delivery)
     */
    String generateVerificationToken(String userUuid);

    /**
     * Verifies the email using the raw token and activates the account if eligible.
     *
     * @param rawToken the raw UUID token from the verify-email link
     */
    void verifyEmail(String rawToken);
}
