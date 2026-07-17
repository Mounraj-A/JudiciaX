package com.courtai.evidence.repository;

import com.courtai.evidence.entity.Evidence;
import com.courtai.common.enums.EvidenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository for Evidence items submitted to a case. */
@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {

    List<Evidence> findByCaseFileIdAndIsDeletedFalse(Long caseFileId);

    List<Evidence> findByCaseFileUuidAndIsDeletedFalse(String caseFileUuid);

    List<Evidence> findByCaseFileIdAndEvidenceTypeAndIsDeletedFalse(Long caseFileId, EvidenceType evidenceType);

    List<Evidence> findByCaseFileIdAndIsAdmittedTrueAndIsDeletedFalse(Long caseFileId);

    Optional<Evidence> findByUuidAndIsDeletedFalse(String uuid);

    long countByCaseFileId(Long caseFileId);
    
    long countByCaseFileIdAndIsVerifiedTrue(Long caseFileId);
}
