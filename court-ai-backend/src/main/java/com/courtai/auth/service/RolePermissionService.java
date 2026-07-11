package com.courtai.auth.service;

import com.courtai.auth.dto.*;
import com.courtai.auth.entity.Role;
import com.courtai.common.enums.PermissionCode;
import com.courtai.common.enums.UserRole;

import java.util.List;

/**
 * Service contract for managing {@link Role} entities and their permission assignments.
 *
 * <p>All mutating operations generate audit log entries and, where appropriate,
 * trigger in-app notifications to affected users.</p>
 */
public interface RolePermissionService {

    // ── READ ─────────────────────────────────────────────────────────────────

    /** Returns the full Role entity for a given enum name (internal use). */
    Role getRoleByName(UserRole roleName);

    /** Returns a list of all active roles as summary DTOs. */
    List<RoleSummaryResponse> getAllRoles();

    /** Returns a full role response (with permissions) by UUID. */
    RoleResponse getRoleByUuid(String uuid);

    /** Returns the permission codes assigned to a role. */
    List<String> getPermissionsForRole(UserRole roleName);

    /** Returns all permissions assigned to a role, as summary DTOs. */
    List<PermissionSummaryResponse> getRolePermissions(String roleUuid);

    // ── WRITE ────────────────────────────────────────────────────────────────

    /** Creates a new custom role. Audits ROLE_CREATED event. */
    RoleResponse createRole(RoleRequest request);

    /** Updates display name / description of an existing role. Audits ROLE_UPDATED event. */
    RoleResponse updateRole(String roleUuid, RoleRequest request);

    /**
     * Soft-deletes a role by UUID.
     *
     * @throws com.courtai.exception.UnauthorizedActionException if the role is a system role.
     */
    void deleteRole(String roleUuid);

    // ── PERMISSION ASSIGNMENT ─────────────────────────────────────────────────

    /** Assigns a permission to a role. Audits PERMISSION_ASSIGNED event. */
    RoleResponse assignPermissionToRole(String roleUuid, AssignPermissionRequest request);

    /**
     * Removes a permission from a role. Audits PERMISSION_REMOVED event.
     * Notifies all users with that role about the change.
     */
    RoleResponse removePermissionFromRole(String roleUuid, String permissionCode);
}

