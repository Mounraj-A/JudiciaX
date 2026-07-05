package com.courtai.auth.service;

import com.courtai.auth.dto.AuthResponse;
import com.courtai.auth.dto.LoginRequest;

/**
 * Service contract for authentication operations.
 *
 * <p>Handles login, token refresh, and logout flows.</p>
 */
public interface AuthService {

    /**
     * Authenticates a user and returns JWT tokens.
     *
     * @param request the login credentials
     * @return JWT access and refresh tokens with user metadata
     */
    AuthResponse login(LoginRequest request);

    /**
     * Issues a new access token using a valid refresh token.
     *
     * @param refreshToken the refresh token
     * @return new JWT access token
     */
    AuthResponse refreshToken(String refreshToken);

    /**
     * Logs out the current user (placeholder for token blacklisting in future).
     *
     * @param accessToken the active access token
     */
    void logout(String accessToken);
}
