package com.courtai.casemanagement.repository;

import com.courtai.casemanagement.entity.CaseTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Repository for the append-only {@link CaseTimeline} event log. */
@Repository
public interface CaseTimelineRepository extends JpaRepository<CaseTimeline, Long> {

    List<CaseTimeline> findByCaseFileIdAndIsDeletedFalseOrderByEventTimeAsc(Long caseFileId);

    @Query("SELECT t FROM CaseTimeline t WHERE t.caseFile.uuid = :caseUuid " +
           "AND t.isDeleted = false ORDER BY t.eventTime ASC")
    List<CaseTimeline> findByCaseUuidOrderByEventTimeAsc(@Param("caseUuid") String caseUuid);

    @Query("SELECT t FROM CaseTimeline t WHERE t.caseFile.uuid = :caseUuid " +
           "AND t.eventType = :eventType AND t.isDeleted = false ORDER BY t.eventTime DESC")
    List<CaseTimeline> findByCaseUuidAndEventType(@Param("caseUuid") String caseUuid,
                                                   @Param("eventType") String eventType);

    @Query("SELECT t FROM CaseTimeline t WHERE t.caseFile.uuid = :caseUuid " +
           "AND t.isDeleted = false ORDER BY t.eventTime DESC")
    List<CaseTimeline> findByCaseUuidOrderByEventTimeDesc(@Param("caseUuid") String caseUuid);
}
