package com.courtai.audit.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "audit_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditAttachment extends BaseEntity {

    @Column(name = "audit_event_uuid", nullable = false, length = 36)
    private String auditEventUuid;

    @Column(name = "file_url", nullable = false, length = 1000)
    private String fileUrl;

    @Column(name = "file_type", length = 50)
    private String fileType;

}
