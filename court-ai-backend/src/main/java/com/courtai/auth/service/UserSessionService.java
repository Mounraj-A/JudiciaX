package com.courtai.auth.service;

import com.courtai.auth.dto.response.SessionResponse;
import com.courtai.auth.entity.UserSession;
import com.courtai.user.entity.User;

import java.util.List;

/**
 * Service for managing user sessions across devices.
 */
public interface UserSessionService {

    /** Creates a new active session on login. */
    UserSession createSession(User user, String accessToken, String refreshToken,
                              String ipAddress, String device, String browser, String os);

    /** Lists all sessions for the authenticated user. */
    List<SessionResponse> getSessionsForUser(User user, String currentAccessToken);

    /** Revokes a specific session by UUID (the owner must be the calling user). */
    void revokeSession(User user, String sessionUuid);

    /** Revokes all active sessions for a user (logout from all devices). */
    void revokeAllSessions(User user);

    /** Updates the access/refresh token hashes when tokens are rotated. */
    void updateTokenHashes(String oldAccessTokenHash, String newAccessToken, String newRefreshToken);
}
