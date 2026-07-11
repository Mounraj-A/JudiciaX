package com.courtai.auth.dto;

import com.courtai.common.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for assigning a role to a user.
 *
 * @param userUuid UUID of the target user.
 * @param role     The role to assign.
 */
public record AssignRoleRequest(

        @NotBlank(message = "User UUID is required")
        String userUuid,

        @NotNull(message = "Role is required")
        UserRole role
) {}
