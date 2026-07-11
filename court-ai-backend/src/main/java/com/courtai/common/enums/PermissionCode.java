package com.courtai.common.enums;

/**
 * Fine-grained permission codes used in RBAC.
 *
 * <p>Permissions are grouped by role module. Each {@link com.courtai.auth.entity.Role}
 * holds a set of these permissions, and {@link com.courtai.auth.entity.Permission}
 * maps each code to a human-readable name and description.</p>
 *
 * <p>Used with {@code @PreAuthorize("hasAuthority('CREATE_CASE')")} for
 * method-level security.</p>
 */
public enum PermissionCode {

    // =========================================================
    //  ADVOCATE PERMISSIONS
    // =========================================================

    CREATE_CASE,
    UPDATE_CASE,
    UPLOAD_DOCUMENT,
    TRACK_CASE,
    DOWNLOAD_ORDER,
    VIEW_CASE_DETAILS,

    // =========================================================
    //  CLERK PERMISSIONS
    // =========================================================

    FILE_CASE,
    ASSIGN_JUDGE,
    MANAGE_SCHEDULE,
    MANAGE_DOCUMENTS,
    VIEW_ALL_CASES,

    // =========================================================
    //  JUDGE PERMISSIONS
    // =========================================================

    VIEW_CASE,
    MODIFY_PRIORITY,
    SCHEDULE_HEARING,
    UPLOAD_ORDER,
    GENERATE_ORDER,
    SET_VERDICT,
    REVIEW_AI_ANALYSIS,
    VIEW_AI_ANALYSIS,

    // =========================================================
    //  ADMIN PERMISSIONS
    // =========================================================

    MANAGE_USERS,
    VIEW_AUDIT,
    SYSTEM_SETTINGS,
    APPROVE_USER,
    REJECT_USER,
    LOCK_USER,
    UNLOCK_USER,
    RESET_PASSWORD,
    ASSIGN_ROLE,
    VIEW_SECURITY_EVENTS,
    VIEW_DASHBOARD,
    MANAGE_NOTIFICATIONS
}
