package com.courtai.audit.repository;

import com.courtai.audit.entity.SecurityAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityAuditRepository extends JpaRepository<SecurityAudit, Long> {
    Optional<SecurityAudit> findByUuidAndIsDeletedFalse(String uuid);
}
