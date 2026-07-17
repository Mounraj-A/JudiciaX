package com.courtai.audit.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent extends BaseEntity {

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "module", length = 50)
    private String module;

    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @Column(name = "entity_type", length = 100)
    private String entityType;

    @Column(name = "entity_uuid", length = 36)
    private String entityUuid;

    @Column(name = "actor_uuid", length = 36)
    private String actorUuid;

    @Column(name = "actor_role", length = 50)
    private String actorRole;

    @Column(name = "actor_name", length = 150)
    private String actorName;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "browser", length = 255)
    private String browser;

    @Column(name = "device", length = 255)
    private String device;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

}
