package com.courtai.auth.service;

import com.courtai.auth.dto.PermissionRequest;
import com.courtai.auth.dto.PermissionResponse;
import com.courtai.common.enums.PermissionCode;

import java.util.List;

/**
 * Service contract for managing {@link com.courtai.auth.entity.Permission} entities.
 *
 * <p>Provides full CRUD capability plus module-based and code-based lookups.
 * All write operations emit audit events.</p>
 */
public interface PermissionService {

    // ── READ ─────────────────────────────────────────────────────────────────

    /** Returns all active permissions. */
    List<PermissionResponse> getAllPermissions();

    /** Returns all active permissions belonging to a module. */
    List<PermissionResponse> getPermissionsByModule(String module);

    /** Returns a single permission by its code. */
    PermissionResponse getPermissionByCode(PermissionCode code);

    /** Returns a single permission by its business UUID. */
    PermissionResponse getPermissionByUuid(String uuid);

    // ── WRITE ────────────────────────────────────────────────────────────────

    /** Creates a new permission. Audits PERMISSION_CREATED event. */
    PermissionResponse createPermission(PermissionRequest request);

    /** Updates name / description of an existing permission. Audits PERMISSION_UPDATED. */
    PermissionResponse updatePermission(String uuid, PermissionRequest request);

    /**
     * Soft-deletes a permission by UUID. Audits PERMISSION_DELETED event.
     *
     * @throws com.courtai.exception.UnauthorizedActionException if the permission is mandatory.
     */
    void deletePermission(String uuid);
}
