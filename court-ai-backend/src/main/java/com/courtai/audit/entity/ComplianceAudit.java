package com.courtai.audit.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "compliance_audits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplianceAudit extends BaseEntity {

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "violation_type", nullable = false, length = 100)
    private String violationType;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "compliance_status", length = 50)
    private String complianceStatus;

}
