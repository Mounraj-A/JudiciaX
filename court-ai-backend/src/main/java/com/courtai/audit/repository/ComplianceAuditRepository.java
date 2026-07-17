package com.courtai.audit.repository;

import com.courtai.audit.entity.ComplianceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComplianceAuditRepository extends JpaRepository<ComplianceAudit, Long> {
    Optional<ComplianceAudit> findByUuidAndIsDeletedFalse(String uuid);
}
