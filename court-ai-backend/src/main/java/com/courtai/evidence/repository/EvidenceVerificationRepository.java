package com.courtai.evidence.repository;

import com.courtai.evidence.entity.EvidenceVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository for EvidenceVerification — court verification decisions on evidence. */
@Repository
public interface EvidenceVerificationRepository extends JpaRepository<EvidenceVerification, Long> {

    Optional<EvidenceVerification> findByEvidenceIdAndIsDeletedFalse(Long evidenceId);

    Optional<EvidenceVerification> findByEvidenceUuidAndIsDeletedFalse(String evidenceUuid);

    List<EvidenceVerification> findByVerificationStatusAndIsDeletedFalse(String verificationStatus);

    Optional<EvidenceVerification> findByUuidAndIsDeletedFalse(String uuid);
}
