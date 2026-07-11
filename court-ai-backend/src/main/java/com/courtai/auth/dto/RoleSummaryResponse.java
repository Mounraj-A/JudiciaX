package com.courtai.auth.dto;

/**
 * Lightweight role summary — used in list views and embedded in UserResponse.
 *
 * @param uuid        Business-facing UUID.
 * @param roleCode    Enum name (e.g., "ROLE_JUDGE").
 * @param displayName Human-readable name.
 */
public record RoleSummaryResponse(
        String uuid,
        String roleCode,
        String displayName
) {}
