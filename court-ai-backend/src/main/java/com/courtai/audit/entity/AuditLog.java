package com.courtai.audit.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Audit log entity — records all significant user actions in the system.
 *
 * <p>Provides a tamper-evident trail for compliance and forensic analysis.</p>
 *
 * <p>Every sensitive operation (login, case creation, document upload, role changes)
 * should be recorded here via the {@link com.courtai.audit.service.AuditService}.</p>
 */
@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_actor_id", columnList = "actor_uuid"),
                @Index(name = "idx_audit_action", columnList = "action"),
                @Index(name = "idx_audit_entity", columnList = "entity_type"),
                @Index(name = "idx_audit_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends BaseEntity {

    @Column(name = "actor_uuid", length = 36)
    private String actorUuid;  // UUID of the user who performed the action

    @Column(name = "actor_email", length = 150)
    private String actorEmail;

    @Column(name = "actor_role", length = 30)
    private String actorRole;

    @Column(name = "action", nullable = false, length = 100)
    private String action;  // e.g., USER_LOGIN, CASE_CREATED, DOCUMENT_UPLOADED

    @Column(name = "entity_type", length = 100)
    private String entityType;  // e.g., CaseFile, User, Document

    @Column(name = "entity_uuid", length = 36)
    private String entityUuid;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "request_id", length = 36)
    private String requestId;  // Correlation ID from MDC

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;  // JSON snapshot before change

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;  // JSON snapshot after change

    @Column(name = "outcome", length = 20)
    private String outcome;  // SUCCESS or FAILURE
}
