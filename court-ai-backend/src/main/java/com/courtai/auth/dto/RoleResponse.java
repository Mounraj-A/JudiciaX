package com.courtai.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Full role response DTO — returned by create, update, and single-get operations.
 *
 * @param uuid        Business-facing UUID (never expose surrogate id).
 * @param roleCode    Enum name of the role (e.g., "ROLE_JUDGE").
 * @param displayName Human-readable role name.
 * @param description Role description.
 * @param isSystemRole Whether this role is a protected system role.
 * @param permissions Set of permission summaries assigned to this role.
 * @param createdAt   Creation timestamp.
 * @param updatedAt   Last update timestamp.
 * @param createdBy   Principal who created the role.
 */
public record RoleResponse(

        String uuid,
        String roleCode,
        String displayName,
        String description,
        boolean isSystemRole,
        Set<PermissionSummaryResponse> permissions,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt,

        String createdBy
) {}
