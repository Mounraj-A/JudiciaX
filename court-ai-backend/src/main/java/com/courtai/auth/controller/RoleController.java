package com.courtai.auth.controller;

import com.courtai.auth.dto.*;
import com.courtai.auth.service.RolePermissionService;
import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Role management APIs.
 *
 * <p>All endpoints require JWT authentication. Write operations require
 * {@code SYSTEM_CONFIGURATION} permission (ROLE_ADMIN only in practice).</p>
 *
 * <p>Base path: {@code /api/v1/rbac/roles}</p>
 */
@Tag(name = "Roles", description = "RBAC Role management APIs")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/rbac/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RolePermissionService rolePermissionService;

    // ── READ ─────────────────────────────────────────────────────────────────

    @Operation(summary = "List all active roles",
               description = "Returns a summary list of all non-deleted roles. Requires MANAGE_USERS.")
    @GetMapping
    @PreAuthorize("hasAuthority('MANAGE_USERS') or hasAuthority('SYSTEM_CONFIGURATION')")
    public ResponseEntity<ApiResponse<List<RoleSummaryResponse>>> getAllRoles() {
        List<RoleSummaryResponse> roles = rolePermissionService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success("Roles retrieved successfully", roles));
    }

    @Operation(summary = "Get role by UUID",
               description = "Returns full role details including all assigned permissions.")
    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('MANAGE_USERS') or hasAuthority('SYSTEM_CONFIGURATION')")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleByUuid(
            @PathVariable String uuid) {
        RoleResponse role = rolePermissionService.getRoleByUuid(uuid);
        return ResponseEntity.ok(ApiResponse.success("Role retrieved successfully", role));
    }

    @Operation(summary = "Get permissions for a role by UUID",
               description = "Returns only the permission summaries assigned to the role.")
    @GetMapping("/{uuid}/permissions")
    @PreAuthorize("hasAuthority('MANAGE_USERS') or hasAuthority('SYSTEM_CONFIGURATION')")
    public ResponseEntity<ApiResponse<List<PermissionSummaryResponse>>> getRolePermissions(
            @PathVariable String uuid) {
        List<PermissionSummaryResponse> permissions = rolePermissionService.getRolePermissions(uuid);
        return ResponseEntity.ok(ApiResponse.success("Role permissions retrieved successfully", permissions));
    }

    // ── WRITE ────────────────────────────────────────────────────────────────

    @Operation(summary = "Create a custom role",
               description = "Creates a new non-system role. Requires SYSTEM_CONFIGURATION.")
    @PostMapping
    @PreAuthorize("hasAuthority('SYSTEM_CONFIGURATION')")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(
            @Valid @RequestBody RoleRequest request) {
        RoleResponse created = rolePermissionService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Role created successfully", created));
    }

    @Operation(summary = "Update role metadata",
               description = "Updates display name and description of a role. System roles can be relabelled but not deleted.")
    @PutMapping("/{uuid}")
    @PreAuthorize("hasAuthority('SYSTEM_CONFIGURATION')")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(
            @PathVariable String uuid,
            @Valid @RequestBody RoleRequest request) {
        RoleResponse updated = rolePermissionService.updateRole(uuid, request);
        return ResponseEntity.ok(ApiResponse.success("Role updated successfully", updated));
    }

    @Operation(summary = "Delete a custom role",
               description = "Soft-deletes a role. System roles (ADMIN, JUDGE, CLERK, ADVOCATE) cannot be deleted.")
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('SYSTEM_CONFIGURATION')")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable String uuid) {
        rolePermissionService.deleteRole(uuid);
        return ResponseEntity.ok(ApiResponse.success("Role deleted successfully"));
    }

    // ── PERMISSION ASSIGNMENT ─────────────────────────────────────────────────

    @Operation(summary = "Assign a permission to a role",
               description = "Idempotent — assigning an already-present permission is a no-op. Requires SYSTEM_CONFIGURATION.")
    @PostMapping("/{uuid}/permissions")
    @PreAuthorize("hasAuthority('SYSTEM_CONFIGURATION')")
    public ResponseEntity<ApiResponse<RoleResponse>> assignPermission(
            @PathVariable String uuid,
            @Valid @RequestBody AssignPermissionRequest request) {
        RoleResponse updated = rolePermissionService.assignPermissionToRole(uuid, request);
        return ResponseEntity.ok(ApiResponse.success("Permission assigned to role successfully", updated));
    }

    @Operation(summary = "Remove a permission from a role",
               description = "Removes the permission immediately. All users with this role lose the permission on next token refresh.")
    @DeleteMapping("/{uuid}/permissions/{permissionCode}")
    @PreAuthorize("hasAuthority('SYSTEM_CONFIGURATION')")
    public ResponseEntity<ApiResponse<RoleResponse>> removePermission(
            @PathVariable String uuid,
            @PathVariable String permissionCode) {
        RoleResponse updated = rolePermissionService.removePermissionFromRole(uuid, permissionCode);
        return ResponseEntity.ok(ApiResponse.success("Permission removed from role successfully", updated));
    }
}
