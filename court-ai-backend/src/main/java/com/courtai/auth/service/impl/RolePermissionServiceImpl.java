package com.courtai.auth.service.impl;

import com.courtai.audit.service.AuditService;
import com.courtai.auth.dto.*;
import com.courtai.auth.entity.Permission;
import com.courtai.auth.entity.Role;
import com.courtai.auth.mapper.PermissionMapper;
import com.courtai.auth.mapper.RoleMapper;
import com.courtai.auth.repository.PermissionRepository;
import com.courtai.auth.repository.RoleRepository;
import com.courtai.auth.service.RolePermissionService;
import com.courtai.common.enums.PermissionCode;
import com.courtai.common.enums.UserRole;
import com.courtai.exception.DuplicateRoleException;
import com.courtai.exception.PermissionNotFoundException;
import com.courtai.exception.RoleNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import com.courtai.notification.service.NotificationService;
import com.courtai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Full enterprise implementation of {@link RolePermissionService}.
 *
 * <p>All writes are transactional and emit audit events.
 * Permission removal additionally notifies affected users via {@link NotificationService}.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RoleRepository        roleRepository;
    private final PermissionRepository  permissionRepository;
    private final RoleMapper            roleMapper;
    private final PermissionMapper      permissionMapper;
    private final AuditService          auditService;
    private final NotificationService   notificationService;
    private final UserRepository        userRepository;

    // =========================================================
    //  READ
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Role getRoleByName(UserRole roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException(roleName.name()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleSummaryResponse> getAllRoles() {
        return roleMapper.toSummaryList(roleRepository.findAllByIsDeletedFalse());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleByUuid(String uuid) {
        Role role = roleRepository.findByUuidWithPermissions(uuid)
                .orElseThrow(() -> new RoleNotFoundException(uuid));
        return roleMapper.toResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getPermissionsForRole(UserRole roleName) {
        return getRoleByName(roleName).getPermissions().stream()
                .map(p -> p.getCode().name())
                .sorted()
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionSummaryResponse> getRolePermissions(String roleUuid) {
        Role role = roleRepository.findByUuidWithPermissions(roleUuid)
                .orElseThrow(() -> new RoleNotFoundException(roleUuid));
        return role.getPermissions().stream()
                .map(permissionMapper::toSummary)
                .sorted(java.util.Comparator.comparing(PermissionSummaryResponse::permissionCode))
                .collect(Collectors.toList());
    }

    // =========================================================
    //  WRITE
    // =========================================================

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        log.info("Creating role: {}", request.displayName());

        Role role = Role.builder()
                .displayName(request.displayName())
                .description(request.description())
                .build();

        Role saved = roleRepository.save(role);
        log.info("Role created with UUID: {}", saved.getUuid());

        auditService.logSuccess(
                "ROLE_CREATED", "Role", saved.getUuid(),
                "Role created: " + request.displayName());

        return roleMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public RoleResponse updateRole(String roleUuid, RoleRequest request) {
        log.info("Updating role: {}", roleUuid);

        Role role = roleRepository.findByUuidAndIsDeletedFalse(roleUuid)
                .orElseThrow(() -> new RoleNotFoundException(roleUuid));

        role.setDisplayName(request.displayName());
        if (request.description() != null) {
            role.setDescription(request.description());
        }

        Role saved = roleRepository.save(role);
        log.info("Role updated: {}", roleUuid);

        auditService.logSuccess(
                "ROLE_UPDATED", "Role", saved.getUuid(),
                "Role updated: " + request.displayName());

        return roleMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteRole(String roleUuid) {
        log.info("Deleting role: {}", roleUuid);

        Role role = roleRepository.findByUuidAndIsDeletedFalse(roleUuid)
                .orElseThrow(() -> new RoleNotFoundException(roleUuid));

        // System roles (those with a mapped UserRole enum) cannot be deleted
        if (role.getName() != null) {
            throw new UnauthorizedActionException(
                    "System role '" + role.getName().name() + "' cannot be deleted.");
        }

        role.softDelete();
        roleRepository.save(role);
        log.info("Role soft-deleted: {}", roleUuid);

        auditService.logSuccess(
                "ROLE_DELETED", "Role", roleUuid,
                "Role deleted: " + role.getDisplayName());
    }

    // =========================================================
    //  PERMISSION ASSIGNMENT
    // =========================================================

    @Override
    @Transactional
    public RoleResponse assignPermissionToRole(String roleUuid, AssignPermissionRequest request) {
        log.info("Assigning permission {} to role {}", request.permissionCode(), roleUuid);

        Role role = roleRepository.findByUuidWithPermissions(roleUuid)
                .orElseThrow(() -> new RoleNotFoundException(roleUuid));

        Permission permission = permissionRepository.findByCodeAndIsDeletedFalse(request.permissionCode())
                .orElseThrow(() -> new PermissionNotFoundException(request.permissionCode().name()));

        boolean alreadyAssigned = role.getPermissions().stream()
                .anyMatch(p -> p.getCode() == request.permissionCode());

        if (!alreadyAssigned) {
            role.getPermissions().add(permission);
            roleRepository.save(role);
            log.info("Permission {} assigned to role {}", request.permissionCode(), roleUuid);

            auditService.logSuccess(
                    "PERMISSION_ASSIGNED", "Role", roleUuid,
                    "Permission " + request.permissionCode().name()
                            + " assigned to role " + role.getDisplayName());
        } else {
            log.debug("Permission {} already exists on role {}", request.permissionCode(), roleUuid);
        }

        return roleMapper.toResponse(role);
    }

    @Override
    @Transactional
    public RoleResponse removePermissionFromRole(String roleUuid, String permissionCode) {
        log.info("Removing permission {} from role {}", permissionCode, roleUuid);

        PermissionCode code = parsePermissionCode(permissionCode);

        Role role = roleRepository.findByUuidWithPermissions(roleUuid)
                .orElseThrow(() -> new RoleNotFoundException(roleUuid));

        Permission toRemove = role.getPermissions().stream()
                .filter(p -> p.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new PermissionNotFoundException(
                        "Permission " + permissionCode + " not assigned to role " + roleUuid));

        role.getPermissions().remove(toRemove);
        roleRepository.save(role);
        log.info("Permission {} removed from role {}", permissionCode, roleUuid);

        auditService.logSuccess(
                "PERMISSION_REMOVED", "Role", roleUuid,
                "Permission " + permissionCode + " removed from role " + role.getDisplayName());

        // Notify all users carrying this role about the permission change
        notifyUsersOfRole(role, permissionCode);

        return roleMapper.toResponse(role);
    }

    // =========================================================
    //  PRIVATE HELPERS
    // =========================================================

    private PermissionCode parsePermissionCode(String code) {
        try {
            return PermissionCode.valueOf(code.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new PermissionNotFoundException("Unknown permission code: " + code);
        }
    }

    private void notifyUsersOfRole(Role role, String permissionCode) {
        if (role.getName() == null) return;
        try {
            userRepository.findByRoleAndIsDeletedFalse(role.getName()).forEach(user ->
                    notificationService.sendInAppNotification(
                            user.getUuid(),
                            "Permission Updated",
                            "The permission '" + permissionCode + "' has been removed from your role '"
                                    + role.getDisplayName() + "'. Your access has been updated.",
                            role.getUuid(),
                            "Role"));
        } catch (Exception ex) {
            log.warn("Failed to send role-permission-change notifications: {}", ex.getMessage());
        }
    }
}

