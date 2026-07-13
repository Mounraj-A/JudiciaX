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

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** Repository for CaseFile — the central aggregate. */
@Repository
public interface CaseFileRepository extends JpaRepository<CaseFile, Long>,
        JpaSpecificationExecutor<CaseFile> {

    Optional<CaseFile> findByCaseNumberAndIsDeletedFalse(String caseNumber);

    Optional<CaseFile> findByUuidAndIsDeletedFalse(String uuid);

    boolean existsByCaseNumber(String caseNumber);

    Page<CaseFile> findByStatusAndIsDeletedFalse(CaseStatus status, Pageable pageable);

    Page<CaseFile> findByCaseTypeAndIsDeletedFalse(CaseType caseType, Pageable pageable);

    @Query("SELECT c FROM CaseFile c WHERE c.assignedJudge.uuid = :judgeUuid AND c.isDeleted = false")
    Page<CaseFile> findByAssignedJudgeUuidAndIsDeletedFalse(@Param("judgeUuid") String judgeUuid, Pageable pageable);

    // ── Advocate-scoped queries (Advocate Portal) ─────────────────────────

    /** Returns all cases where the advocate is petitioner OR respondent. */
    @Query("""
            SELECT c FROM CaseFile c
            WHERE (c.petitionerAdvocate.uuid = :advocateUuid
               OR (c.respondentAdvocate IS NOT NULL AND c.respondentAdvocate.uuid = :advocateUuid))
              AND c.isDeleted = false
            """)
    Page<CaseFile> findByAdvocateUuid(@Param("advocateUuid") String advocateUuid, Pageable pageable);

    /** Cases for advocate filtered by status. */
    @Query("""
            SELECT c FROM CaseFile c
            WHERE (c.petitionerAdvocate.uuid = :advocateUuid
               OR (c.respondentAdvocate IS NOT NULL AND c.respondentAdvocate.uuid = :advocateUuid))
              AND c.status = :status
              AND c.isDeleted = false
            """)
    Page<CaseFile> findByAdvocateUuidAndStatus(
            @Param("advocateUuid") String advocateUuid,
            @Param("status") CaseStatus status,
            Pageable pageable);

    /** Keyword search across title and party names for a given advocate. */
    @Query("""
            SELECT c FROM CaseFile c
            WHERE (c.petitionerAdvocate.uuid = :advocateUuid
               OR (c.respondentAdvocate IS NOT NULL AND c.respondentAdvocate.uuid = :advocateUuid))
              AND c.isDeleted = false
              AND (LOWER(c.caseTitle)       LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.petitionerName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.respondentName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.caseNumber)     LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<CaseFile> searchByAdvocateUuidAndKeyword(
            @Param("advocateUuid") String advocateUuid,
            @Param("keyword") String keyword,
            Pageable pageable);

    /** Ownership check — validates a case belongs to the advocate before update/delete. */
    @Query("""
            SELECT COUNT(c) > 0 FROM CaseFile c
            WHERE c.uuid = :caseUuid
              AND (c.petitionerAdvocate.uuid = :advocateUuid
               OR (c.respondentAdvocate IS NOT NULL AND c.respondentAdvocate.uuid = :advocateUuid))
              AND c.isDeleted = false
            """)
    boolean existsByUuidAndAdvocateUuid(
            @Param("caseUuid") String caseUuid,
            @Param("advocateUuid") String advocateUuid);

    // ── Clerk-scoped queries (Clerk Portal) ──────────────────────────────

    /** Pending cases for a specific court awaiting clerk scrutiny. */
    @Query("""
            SELECT c FROM CaseFile c
            WHERE c.status IN :statuses
              AND c.court.id = :courtId
              AND c.isDeleted = false
            """)
    Page<CaseFile> findByStatusInAndCourtIdAndIsDeletedFalse(
            @Param("statuses") Collection<CaseStatus> statuses,
            @Param("courtId") Long courtId,
            Pageable pageable);

    /** Clerk keyword search within their court. */
    @Query("""
            SELECT c FROM CaseFile c
            WHERE c.court.id = :courtId
              AND c.isDeleted = false
              AND (LOWER(c.caseTitle)        LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.petitionerName)  LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.respondentName)  LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.caseNumber)      LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.officialCaseNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<CaseFile> searchByCourtIdAndKeyword(
            @Param("courtId") Long courtId,
            @Param("keyword") String keyword,
            Pageable pageable);

    /**
     * Duplicate detection — finds cases in the same court with similar parties/type.
     * Excludes the case being checked by its own UUID.
     */
    @Query("""
            SELECT c FROM CaseFile c
            WHERE c.court.id = :courtId
              AND c.caseType = :caseType
              AND c.isDeleted = false
              AND c.uuid != :excludeUuid
              AND (
                  (LOWER(c.petitionerName) LIKE LOWER(CONCAT('%', :petitioner, '%'))
                   AND LOWER(c.respondentName) LIKE LOWER(CONCAT('%', :respondent, '%')))
                OR
                  (LOWER(c.petitionerName) LIKE LOWER(CONCAT('%', :respondent, '%'))
                   AND LOWER(c.respondentName) LIKE LOWER(CONCAT('%', :petitioner, '%')))
              )
            """)
    List<CaseFile> findPotentialDuplicates(
            @Param("courtId") Long courtId,
            @Param("caseType") com.courtai.common.enums.CaseType caseType,
            @Param("petitioner") String petitioner,
            @Param("respondent") String respondent,
            @Param("excludeUuid") String excludeUuid);

    /** Count cases registered in a court on a given date — for dashboard. */
    @Query("""
            SELECT COUNT(c) FROM CaseFile c
            WHERE c.court.id = :courtId
              AND c.status = :status
              AND DATE(c.registeredAt) = :date
              AND c.isDeleted = false
            """)
    long countByCourtIdAndStatusAndRegisteredAtDate(
            @Param("courtId") Long courtId,
            @Param("status") CaseStatus status,
            @Param("date") LocalDate date);

    /** Count cases with judge queue position assigned but no judge yet. */
    @Query("""
            SELECT COUNT(c) FROM CaseFile c
            WHERE c.court.id = :courtId
              AND c.judgeQueuePosition IS NOT NULL
              AND c.assignedJudge IS NULL
              AND c.isDeleted = false
            """)
    long countPendingJudgeAssignment(@Param("courtId") Long courtId);
}
