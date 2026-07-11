package com.courtai.casefile.repository;

import com.courtai.casefile.entity.CaseFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Repository for CaseFlag — boolean feature flags per case. */
@Repository
public interface CaseFlagRepository extends JpaRepository<CaseFlag, Long> {

    Optional<CaseFlag> findByCaseFileIdAndIsDeletedFalse(Long caseFileId);

    Optional<CaseFlag> findByCaseFileUuidAndIsDeletedFalse(String caseFileUuid);

    Optional<CaseFlag> findByUuidAndIsDeletedFalse(String uuid);
}
