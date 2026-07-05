package com.courtai.audit.service;

/**
 * Service contract for recording system audit events.
 *
 * <p>All significant user actions must be recorded through this service.
 * Implementation will be completed in Phase 2.</p>
 */
public interface AuditService {

    /**
     * Records a successful audit event.
     *
     * @param action      the action performed (e.g., USER_LOGIN, CASE_CREATED)
     * @param entityType  the type of the affected entity
     * @param entityUuid  the UUID of the affected entity
     * @param description human-readable description
     */
    void logSuccess(String action, String entityType, String entityUuid, String description);

    /**
     * Records a failed/error audit event.
     *
     * @param action      the action attempted
     * @param entityType  the type of the affected entity
     * @param entityUuid  the UUID of the affected entity
     * @param description human-readable description of the failure
     */
    void logFailure(String action, String entityType, String entityUuid, String description);
}
