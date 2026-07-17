package com.courtai.hearing.repository;

import com.courtai.hearing.entity.Hearing;
import com.courtai.common.enums.HearingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** Repository for Hearing — court hearing sessions for a case. */
@Repository
public interface HearingRepository extends JpaRepository<Hearing, Long> {

    List<Hearing> findByCaseFileIdAndIsDeletedFalseOrderByScheduledAtDesc(Long caseFileId);

    List<Hearing> findByCaseFileIdAndStatusAndIsDeletedFalse(Long caseFileId, HearingStatus status);

    Page<Hearing> findByStatusAndIsDeletedFalse(HearingStatus status, Pageable pageable);

    List<Hearing> findByScheduledAtBetweenAndIsDeletedFalse(LocalDateTime from, LocalDateTime to);

    Optional<Hearing> findByUuidAndIsDeletedFalse(String uuid);

    /** Batch load hearings for multiple cases — used by advocate portal. */
    List<Hearing> findByCaseFileIdInAndIsDeletedFalseOrderByScheduledAtDesc(Collection<Long> caseFileIds);

    long countByCaseFileId(Long caseFileId);
}
