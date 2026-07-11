package com.courtai.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating or updating a Role.
 *
 * @param displayName Human-readable name shown in UI (e.g., "Senior Judge").
 * @param description Optional description of the role's responsibilities.
 */
public record RoleRequest(

        @NotBlank(message = "Display name is required")
        @Size(min = 3, max = 100, message = "Display name must be between 3 and 100 characters")
        String displayName,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description
) {}
