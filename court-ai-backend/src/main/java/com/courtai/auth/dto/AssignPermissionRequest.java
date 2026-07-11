package com.courtai.auth.dto;

import com.courtai.common.enums.PermissionCode;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for assigning a permission to a role.
 *
 * @param permissionCode The {@link PermissionCode} to assign.
 */
public record AssignPermissionRequest(

        @NotNull(message = "Permission code is required")
        PermissionCode permissionCode
) {}
