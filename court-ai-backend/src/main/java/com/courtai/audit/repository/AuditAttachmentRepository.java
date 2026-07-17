package com.courtai.audit.repository;

import com.courtai.audit.entity.AuditAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditAttachmentRepository extends JpaRepository<AuditAttachment, Long> {
    List<AuditAttachment> findByAuditEventUuidAndIsDeletedFalse(String auditEventUuid);
}
