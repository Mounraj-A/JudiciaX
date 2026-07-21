package com.courtai.admin.service;

import com.courtai.auth.dto.response.UserProfileResponse;
import com.courtai.common.enums.UserRole;
import com.courtai.user.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Admin service for managing user accounts — approval, rejection, lock/unlock, role assignment.
 */
public interface AdminUserService {

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserProfileResponse getUserByUuid(String uuid);

    /** Approves a pending user account (sets status to ACTIVE). */
    void approveUser(String uuid, String adminUuid);

    /** Rejects a pending user account (sets status to SUSPENDED). */
    void rejectUser(String uuid, String reason, String adminUuid);

    /** Manually locks a user account (sets status to LOCKED). */
    void lockUser(String uuid, String reason, String adminUuid);

    /** Unlocks a locked/suspended user account (sets status to ACTIVE). */
    void unlockUser(String uuid, String adminUuid);

    /** Admin-initiated password reset (generates a reset token and returns it). */
    String adminResetPassword(String uuid, String adminUuid);

    /** Assigns a new role to a user. */
    void assignRole(String uuid, UserRole newRole, String adminUuid);

    /** Admin soft-delete — sets status to SOFT_DELETED and marks isDeleted. */
    void deleteUser(String uuid, String adminUuid);

    /** Create a new user from Admin panel */
    UserResponse createUser(String email, String fullName, String phone, String password, com.courtai.common.enums.UserRole role, String adminUuid);
}
