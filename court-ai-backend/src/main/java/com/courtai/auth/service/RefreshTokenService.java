package com.courtai.auth.service;

import com.courtai.auth.dto.response.RefreshTokenResponse;

/**
 * Service for refresh token rotation — issues new token pairs and invalidates old ones.
 */
public interface RefreshTokenService {

    /**
     * Rotates the refresh token: validates the old token, marks it used,
     * issues a fresh access + refresh token pair, and updates the session.
     *
     * @param rawRefreshToken the raw refresh token from the client
     * @return new token pair
     */
    RefreshTokenResponse rotate(String rawRefreshToken);

    /** Revokes all refresh tokens for a given user (called on logout-all). */
    void revokeAllForUser(Long userId);

    /** Revokes all refresh tokens associated with a session. */
    void revokeForSession(String sessionId);
}
