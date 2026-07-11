package com.courtai.ai.repository;

import com.courtai.ai.entity.CaseAIQueue;
import com.courtai.common.enums.QueueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository for CaseAIQueue — AI analysis job queue. */
@Repository
public interface CaseAIQueueRepository extends JpaRepository<CaseAIQueue, Long> {

    /** Returns pending jobs ordered by creation time — used by the queue processor. */
    Page<CaseAIQueue> findByQueueStatusOrderByCreatedAtAsc(QueueStatus queueStatus, Pageable pageable);

    List<CaseAIQueue> findByCaseFileIdOrderByCreatedAtDesc(Long caseFileId);

    Optional<CaseAIQueue> findFirstByCaseFileIdAndQueueStatus(Long caseFileId, QueueStatus queueStatus);

    Optional<CaseAIQueue> findByUuidAndIsDeletedFalse(String uuid);

    long countByQueueStatus(QueueStatus queueStatus);
}
