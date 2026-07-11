package com.courtai.auth.service;

import com.courtai.auth.dto.AssignRoleRequest;
import com.courtai.common.enums.UserRole;
import com.courtai.user.entity.User;

import java.util.List;

/**
 * Service contract for assigning and removing roles on {@link User} entities.
 *
 * <p>All operations emit audit events. Role assignment also triggers
 * an in-app notification to the target user.</p>
 */
public interface UserRoleAssignmentService {

    /**
     * Assigns a new role to the user identified by {@code userUuid}.
     * Audits ROLE_ASSIGNED event and notifies the user.
     *
     * @param request contains userUuid and the target role
     * @return the updated User entity
     */
    User assignRoleToUser(AssignRoleRequest request);

    /**
     * Removes a non-ADMIN role from the user, resetting them to ROLE_ADVOCATE.
     *
     * @param userUuid UUID of the target user
     * @return the updated User entity
     */
    User removeRoleFromUser(String userUuid);

    /**
     * Returns all active users holding the given role.
     *
     * @param role the role to filter by
     */
    List<User> getUsersByRole(UserRole role);
}
