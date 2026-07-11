package com.courtai.casefile.repository;

import com.courtai.casefile.entity.CaseStatusHistory;
import com.courtai.common.enums.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository for CaseStatusHistory — append-only status transition audit trail. */
@Repository
public interface CaseStatusHistoryRepository extends JpaRepository<CaseStatusHistory, Long> {

    /** Returns full status timeline for a case, newest first. */
    List<CaseStatusHistory> findByCaseFileIdOrderByChangedAtDesc(Long caseFileId);

    /** Returns full status timeline by case UUID. */
    List<CaseStatusHistory> findByCaseFileUuidOrderByChangedAtDesc(String caseFileUuid);

    /** Returns the most recent status entry for a case. */
    Optional<CaseStatusHistory> findFirstByCaseFileIdOrderByChangedAtDesc(Long caseFileId);

    List<CaseStatusHistory> findByCaseFileIdAndToStatus(Long caseFileId, CaseStatus toStatus);
}
