package com.courtai.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating or updating a Permission.
 *
 * @param permissionCode Unique code matching a {@link com.courtai.common.enums.PermissionCode} enum value.
 * @param permissionName Human-readable name (e.g., "Create Case").
 * @param module         Module this permission belongs to (e.g., "CASE").
 * @param description    Optional description.
 */
public record PermissionRequest(

        @NotBlank(message = "Permission code is required")
        @Size(max = 60, message = "Permission code must not exceed 60 characters")
        String permissionCode,

        @NotBlank(message = "Permission name is required")
        @Size(max = 100, message = "Permission name must not exceed 100 characters")
        String permissionName,

        @NotBlank(message = "Module is required")
        @Size(max = 100, message = "Module must not exceed 100 characters")
        String module,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description
) {}
