package com.courtai.auth.service.impl;

import com.courtai.audit.service.AuditService;
import com.courtai.auth.dto.PermissionRequest;
import com.courtai.auth.dto.PermissionResponse;
import com.courtai.auth.entity.Permission;
import com.courtai.auth.mapper.PermissionMapper;
import com.courtai.auth.repository.PermissionRepository;
import com.courtai.auth.service.PermissionService;
import com.courtai.common.enums.PermissionCode;
import com.courtai.exception.DuplicatePermissionException;
import com.courtai.exception.PermissionNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Full implementation of {@link PermissionService}.
 *
 * <p>Guards mandatory system permissions from deletion.
 * Every write operation emits a structured audit event.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper     permissionMapper;
    private final AuditService         auditService;

    // =========================================================
    //  READ
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        return permissionMapper.toResponseList(permissionRepository.findAllByIsDeletedFalse());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissionsByModule(String module) {
        return permissionMapper.toResponseList(
                permissionRepository.findByModuleAndIsDeletedFalse(module));
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse getPermissionByCode(PermissionCode code) {
        Permission permission = permissionRepository.findByCodeAndIsDeletedFalse(code)
                .orElseThrow(() -> new PermissionNotFoundException(code.name()));
        return permissionMapper.toResponse(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse getPermissionByUuid(String uuid) {
        Permission permission = permissionRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new PermissionNotFoundException(uuid));
        return permissionMapper.toResponse(permission);
    }

    // =========================================================
    //  WRITE
    // =========================================================

    @Override
    @Transactional
    public PermissionResponse createPermission(PermissionRequest request) {
        log.info("Creating permission: {}", request.permissionCode());

        PermissionCode code;
        try {
            code = PermissionCode.valueOf(request.permissionCode().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new PermissionNotFoundException(
                    "Permission code '" + request.permissionCode() + "' is not a valid PermissionCode enum value.");
        }

        if (permissionRepository.existsByCode(code)) {
            throw new DuplicatePermissionException(request.permissionCode());
        }

        Permission permission = Permission.builder()
                .code(code)
                .name(request.permissionName())
                .description(request.description())
                .module(request.module())
                .build();

        Permission saved = permissionRepository.save(permission);
        log.info("Permission created: {} (UUID={})", code, saved.getUuid());

        auditService.logSuccess(
                "PERMISSION_CREATED", "Permission", saved.getUuid(),
                "Permission created: " + code.name());

        return permissionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PermissionResponse updatePermission(String uuid, PermissionRequest request) {
        log.info("Updating permission: {}", uuid);

        Permission permission = permissionRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new PermissionNotFoundException(uuid));

        permission.setName(request.permissionName());
        permission.setModule(request.module());
        if (request.description() != null) {
            permission.setDescription(request.description());
        }

        Permission saved = permissionRepository.save(permission);
        log.info("Permission updated: {}", uuid);

        auditService.logSuccess(
                "PERMISSION_UPDATED", "Permission", uuid,
                "Permission updated: " + saved.getCode().name());

        return permissionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deletePermission(String uuid) {
        log.info("Deleting permission: {}", uuid);

        Permission permission = permissionRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new PermissionNotFoundException(uuid));

        // Mandatory system permissions (seeded in V13) cannot be deleted
        if (isMandatoryPermission(permission.getCode())) {
            throw new UnauthorizedActionException(
                    "System permission '" + permission.getCode().name() + "' cannot be deleted.");
        }

        permission.softDelete();
        permissionRepository.save(permission);
        log.info("Permission soft-deleted: {}", uuid);

        auditService.logSuccess(
                "PERMISSION_DELETED", "Permission", uuid,
                "Permission deleted: " + permission.getCode().name());
    }

    // =========================================================
    //  PRIVATE HELPERS
    // =========================================================

    /**
     * Returns {@code true} for the core seed permissions that must never be removed.
     */
    private boolean isMandatoryPermission(PermissionCode code) {
        return switch (code) {
            case CREATE_CASE, VIEW_CASE, UPLOAD_DOCUMENT, ASSIGN_ROLE,
                 MANAGE_USERS, VIEW_AUDIT, SYSTEM_SETTINGS, LOCK_USER, UNLOCK_USER, RESET_PASSWORD
                    -> true;
            default -> false;
        };
    }
}
