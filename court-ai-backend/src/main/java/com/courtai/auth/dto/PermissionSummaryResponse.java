package com.courtai.auth.dto;

/**
 * Lightweight permission summary — embedded inside {@link RoleResponse}.
 *
 * @param permissionCode Unique permission code (e.g., "CREATE_CASE").
 * @param permissionName Human-readable name.
 * @param module         Module grouping.
 */
public record PermissionSummaryResponse(
        String permissionCode,
        String permissionName,
        String module
) {}
