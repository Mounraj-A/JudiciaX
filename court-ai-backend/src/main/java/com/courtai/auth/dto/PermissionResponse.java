package com.courtai.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Full permission response DTO.
 *
 * @param uuid           Business-facing UUID.
 * @param permissionCode Unique code (e.g., "CREATE_CASE").
 * @param permissionName Human-readable name.
 * @param module         Module grouping (e.g., "CASE").
 * @param description    Description of what this permission allows.
 * @param isActive       Whether this permission is currently active.
 * @param createdAt      Creation timestamp.
 */
public record PermissionResponse(

        String uuid,
        String permissionCode,
        String permissionName,
        String module,
        String description,
        boolean isActive,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt
) {}
