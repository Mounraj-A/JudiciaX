package com.courtai.ai.repository;

import com.courtai.ai.entity.CaseAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Repository for CaseAnalysis — AI model results for a case. */
@Repository
public interface CaseAnalysisRepository extends JpaRepository<CaseAnalysis, Long> {

    Optional<CaseAnalysis> findByCaseFileId(Long caseFileId);

    Optional<CaseAnalysis> findByCaseFileUuid(String caseFileUuid);

    Optional<CaseAnalysis> findByUuidAndIsDeletedFalse(String uuid);
}
