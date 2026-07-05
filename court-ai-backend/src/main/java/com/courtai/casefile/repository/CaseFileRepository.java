package com.courtai.casefile.repository;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.common.enums.CaseStatus;
import com.courtai.common.enums.CaseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Repository for CaseFile — the central aggregate. */
@Repository
public interface CaseFileRepository extends JpaRepository<CaseFile, Long> {

    Optional<CaseFile> findByCaseNumberAndIsDeletedFalse(String caseNumber);

    Optional<CaseFile> findByUuidAndIsDeletedFalse(String uuid);

    boolean existsByCaseNumber(String caseNumber);

    Page<CaseFile> findByStatusAndIsDeletedFalse(CaseStatus status, Pageable pageable);

    Page<CaseFile> findByCaseTypeAndIsDeletedFalse(CaseType caseType, Pageable pageable);

    @Query("SELECT c FROM CaseFile c WHERE c.assignedJudge.uuid = :judgeUuid AND c.isDeleted = false")
    Page<CaseFile> findByAssignedJudgeUuidAndIsDeletedFalse(@Param("judgeUuid") String judgeUuid, Pageable pageable);
}
