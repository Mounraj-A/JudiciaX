package com.courtai.user.service;

import com.courtai.auth.dto.response.SessionResponse;
import com.courtai.auth.dto.response.UserProfileResponse;
import com.courtai.user.dto.CreateUserRequest;
import com.courtai.user.dto.PrivacySettingsRequest;
import com.courtai.user.dto.UpdateProfileRequest;
import com.courtai.user.dto.UserResponse;
import com.courtai.auth.dto.request.ChangePasswordRequest;
import com.courtai.security.UserPrincipal;

import java.util.List;

/**
 * Service contract for user management operations.
 *
 * <p>Business logic will be implemented in {@link UserServiceImpl}.
 * Controllers must depend on this interface, not the implementation.</p>
 */
public interface UserService {

    /** Creates a new user account (admin use). */
    UserResponse createUser(CreateUserRequest request);

    /** Retrieves a user by their public UUID. */
    UserResponse getUserByUuid(String uuid);

    /** Retrieves all active users in the system. */
    List<UserResponse> getAllUsers();

    /** Soft-deletes a user account by UUID. */
    void deleteUser(String uuid);

    // =========================================================
    //  AUTHENTICATED USER OPERATIONS
    // =========================================================

    /** Returns the full profile of the currently authenticated user. */
    UserProfileResponse getMyProfile(UserPrincipal principal);

    /** Updates the authenticated user's own profile fields. */
    UserProfileResponse updateMyProfile(UserPrincipal principal, UpdateProfileRequest request);

    /** Changes the authenticated user's password (enforces history policy). */
    void changePassword(UserPrincipal principal, ChangePasswordRequest request);

    /** Returns all active sessions for the authenticated user. */
    List<SessionResponse> getMySessions(UserPrincipal principal, String currentAccessToken);

    /** Revokes a specific session by UUID. */
    void revokeSession(UserPrincipal principal, String sessionUuid);

    /** Revokes all active sessions for the authenticated user. */
    void revokeAllSessions(UserPrincipal principal);

    /** Updates the authenticated user's privacy settings. */
    void updatePrivacySettings(UserPrincipal principal, PrivacySettingsRequest request);
}
