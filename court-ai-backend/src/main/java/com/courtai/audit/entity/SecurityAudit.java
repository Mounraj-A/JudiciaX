package com.courtai.audit.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "security_audits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityAudit extends BaseEntity {

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

}
