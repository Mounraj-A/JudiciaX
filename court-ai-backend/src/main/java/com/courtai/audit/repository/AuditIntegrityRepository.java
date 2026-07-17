package com.courtai.audit.repository;

import com.courtai.audit.entity.AuditIntegrity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuditIntegrityRepository extends JpaRepository<AuditIntegrity, Long> {
    Optional<AuditIntegrity> findByAuditEventUuidAndIsDeletedFalse(String auditEventUuid);
    Optional<AuditIntegrity> findTopByIsDeletedFalseOrderByCreatedAtDesc();
}
