package com.courtai.audit.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "audit_integrity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditIntegrity extends BaseEntity {

    @Column(name = "audit_event_uuid", nullable = false, length = 36)
    private String auditEventUuid;

    @Column(name = "previous_hash", length = 256)
    private String previousHash;

    @Column(name = "current_hash", nullable = false, length = 256)
    private String currentHash;

    @Column(name = "verification_status", length = 50)
    private String verificationStatus;

}
