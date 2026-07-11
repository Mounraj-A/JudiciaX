package com.courtai.casefile.repository;

import com.courtai.casefile.entity.CaseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository for CaseAssignment — judge assignment history per case. */
@Repository
public interface CaseAssignmentRepository extends JpaRepository<CaseAssignment, Long> {

    /** Returns the current active assignment for a case. */
    Optional<CaseAssignment> findByCaseFileIdAndIsActiveTrueAndIsDeletedFalse(Long caseFileId);

    /** Returns all assignment history for a case ordered by assignment time. */
    List<CaseAssignment> findByCaseFileIdAndIsDeletedFalseOrderByAssignedAtDesc(Long caseFileId);

    /** Returns all active cases assigned to a specific judge. */
    List<CaseAssignment> findByJudgeIdAndIsActiveTrueAndIsDeletedFalse(Long judgeId);

    Optional<CaseAssignment> findByUuidAndIsDeletedFalse(String uuid);
}
