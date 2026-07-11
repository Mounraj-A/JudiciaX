package com.courtai.common.enums;

/**
 * Functional module groupings for fine-grained permissions.
 *
 * <p>Each {@link PermissionCode} belongs to one of these modules, enabling
 * module-level permission queries and Swagger grouping in the RBAC APIs.</p>
 */
public enum PermissionModule {

    /** Authentication operations — login, logout, profile. */
    AUTHENTICATION,

    /** User lifecycle management — create, update, lock, role assignment. */
    USER,

    /** Case management — filing, tracking, assignment, closure. */
    CASE,

    /** Document operations — upload, download, verify, reject. */
    DOCUMENT,

    /** Court administration. */
    COURT,

    /** Hearing scheduling and management. */
    HEARING,

    /** Judge-specific operations — priority, orders, AI analysis. */
    JUDGE,

    /** Clerk-specific operations — verification, registration. */
    CLERK,

    /** Advocate-specific operations — case tracking, submissions. */
    ADVOCATE,

    /** Notification management — view, send, delete. */
    NOTIFICATION,

    /** Reporting and analytics. */
    REPORT,

    /** Audit log access. */
    AUDIT,

    /** System-level configuration and administration. */
    SYSTEM,

    /** AI-driven analysis and recommendations. */
    AI
}
