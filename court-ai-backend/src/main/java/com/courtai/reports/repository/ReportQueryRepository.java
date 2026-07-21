package com.courtai.reports.repository;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.common.enums.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Read-only aggregate query repository for the Reports & Analytics module.
 *
 * <p><strong>Important:</strong> This repository is read-only. Every query here is
 * a SELECT aggregate that cannot be expressed cleanly via existing repositories.
 * No INSERT/UPDATE/DELETE is ever called on this repository.</p>
 *
 * <p>All queries operate on the {@code case_files} table and its JOINs.</p>
 */
@Repository
public interface ReportQueryRepository extends JpaRepository<CaseFile, Long> {

    // ── Count Aggregates ──────────────────────────────────────────────────

    @Query("SELECT COUNT(c) FROM CaseFile c WHERE c.isDeleted = false AND c.status = :status")
    long countByStatus(@Param("status") CaseStatus status);

    @Query("SELECT COUNT(c) FROM CaseFile c WHERE c.isDeleted = false AND c.court.id = :courtId")
    long countByCourt(@Param("courtId") Long courtId);

    @Query("SELECT COUNT(c) FROM CaseFile c WHERE c.isDeleted = false AND c.court.id = :courtId AND c.status = :status")
    long countByCourtAndStatus(@Param("courtId") Long courtId, @Param("status") CaseStatus status);

    // ── Group-By Aggregates ───────────────────────────────────────────────

    /**
     * Returns [caseType (String), count (Long)] tuples for all non-deleted cases.
     */
    @Query("SELECT c.caseType, COUNT(c) FROM CaseFile c WHERE c.isDeleted = false GROUP BY c.caseType")
    List<Object[]> countGroupedByCaseType();

    /**
     * Returns [status (String), count (Long)] tuples.
     */
    @Query("SELECT c.status, COUNT(c) FROM CaseFile c WHERE c.isDeleted = false GROUP BY c.status")
    List<Object[]> countGroupedByStatus();

    /**
     * Returns [priority (String), count (Long)] tuples.
     */
    @Query("SELECT c.priority, COUNT(c) FROM CaseFile c WHERE c.isDeleted = false GROUP BY c.priority")
    List<Object[]> countGroupedByPriority();

    /**
     * Returns [courtName (String), count (Long)] tuples for pending cases by court.
     */
    @Query("""
            SELECT c.court.courtName, COUNT(c)
            FROM CaseFile c
            WHERE c.isDeleted = false
              AND c.status NOT IN ('DISPOSED', 'CLOSED', 'ARCHIVED')
            GROUP BY c.court.courtName
            ORDER BY COUNT(c) DESC
            """)
    List<Object[]> countPendingGroupedByCourt();

    /**
     * Returns [categoryName (String), avgScore (Double)] for priority score by category.
     */
    @Query("""
            SELECT c.caseCategory.categoryName, AVG(c.priorityScore)
            FROM CaseFile c
            WHERE c.isDeleted = false
              AND c.priorityScore IS NOT NULL
              AND c.caseCategory IS NOT NULL
            GROUP BY c.caseCategory.categoryName
            """)
    List<Object[]> avgPriorityScoreGroupedByCategory();

    /**
     * Returns [courtName (String), avgScore (Double)] for priority score by court.
     */
    @Query("""
            SELECT c.court.courtName, AVG(c.priorityScore)
            FROM CaseFile c
            WHERE c.isDeleted = false
              AND c.priorityScore IS NOT NULL
              AND c.court IS NOT NULL
            GROUP BY c.court.courtName
            """)
    List<Object[]> avgPriorityScoreGroupedByCourt();

    /**
     * Returns [judgeName (String), avgScore (Double)] for priority score by judge.
     */
    @Query("""
            SELECT c.assignedJudge.user.username, AVG(c.priorityScore)
            FROM CaseFile c
            WHERE c.isDeleted = false
              AND c.priorityScore IS NOT NULL
              AND c.assignedJudge IS NOT NULL
            GROUP BY c.assignedJudge.user.username
            """)
    List<Object[]> avgPriorityScoreGroupedByJudge();

    // ── Monthly Trend Aggregates ───────────────────────────────────────────

    /**
     * Returns [year (Integer), month (Integer), count (Long)] for filing trend.
     */
    @Query("""
            SELECT YEAR(c.filingDate), MONTH(c.filingDate), COUNT(c)
            FROM CaseFile c
            WHERE c.isDeleted = false
              AND c.filingDate IS NOT NULL
              AND YEAR(c.filingDate) = :year
            GROUP BY YEAR(c.filingDate), MONTH(c.filingDate)
            ORDER BY MONTH(c.filingDate)
            """)
    List<Object[]> monthlyFilingTrend(@Param("year") int year);

    /**
     * Returns [year (Integer), month (Integer), count (Long)] for disposal trend.
     * Proxy for disposal: cases that transitioned to DISPOSED in a given month.
     */
    @Query("""
            SELECT YEAR(c.updatedAt), MONTH(c.updatedAt), COUNT(c)
            FROM CaseFile c
            WHERE c.isDeleted = false
              AND c.status = 'DISPOSED'
              AND c.updatedAt IS NOT NULL
              AND YEAR(c.updatedAt) = :year
            GROUP BY YEAR(c.updatedAt), MONTH(c.updatedAt)
            ORDER BY MONTH(c.updatedAt)
            """)
    List<Object[]> monthlyDisposalTrend(@Param("year") int year);

    // ── Age / Delay Aggregates ─────────────────────────────────────────────

    @Query(value = """
            SELECT AVG(CURRENT_DATE - c.filing_date)
            FROM cases c
            WHERE c.is_deleted = false
              AND c.filing_date IS NOT NULL
              AND c.status NOT IN ('DISPOSED', 'CLOSED', 'ARCHIVED')
            """, nativeQuery = true)
    Double avgAgeInDaysForPendingCases();

    /**
     * Returns [ageBucket (String), count (Long)].
     * Buckets: LT30, D30_90, D90_180, D180_365, GT365
     */
    @Query(value = """
            SELECT
              CASE
                WHEN (CURRENT_DATE - c.filing_date) < 30    THEN 'LT30'
                WHEN (CURRENT_DATE - c.filing_date) < 90    THEN 'D30_90'
                WHEN (CURRENT_DATE - c.filing_date) < 180   THEN 'D90_180'
                WHEN (CURRENT_DATE - c.filing_date) < 365   THEN 'D180_365'
                ELSE 'GT365'
              END,
              COUNT(c.id)
            FROM cases c
            WHERE c.is_deleted = false
              AND c.filing_date IS NOT NULL
            GROUP BY
              CASE
                WHEN (CURRENT_DATE - c.filing_date) < 30    THEN 'LT30'
                WHEN (CURRENT_DATE - c.filing_date) < 90    THEN 'D30_90'
                WHEN (CURRENT_DATE - c.filing_date) < 180   THEN 'D90_180'
                WHEN (CURRENT_DATE - c.filing_date) < 365   THEN 'D180_365'
                ELSE 'GT365'
              END
            """, nativeQuery = true)
    List<Object[]> caseAgeDistribution();

    // ── Court-specific Aggregates ─────────────────────────────────────────

    @Query("SELECT AVG(c.priorityScore) FROM CaseFile c WHERE c.isDeleted = false AND c.court.id = :courtId AND c.priorityScore IS NOT NULL")
    Double avgPriorityScoreByCourtId(@Param("courtId") Long courtId);

    @Query("SELECT COUNT(c) FROM CaseFile c WHERE c.court.id = :courtId AND c.isDeleted = false AND c.filingDate = :date")
    long countByCourtIdAndFilingDate(@Param("courtId") Long courtId, @Param("date") LocalDate date);

    // ── Research Dataset Projection ───────────────────────────────────────

    /**
     * Lightweight projection for the research dataset.
     * Returns [caseUuid, caseType, status, priority, priorityScore, filingDate, courtCode, state]
     * for cases that have been registered (i.e., have official case numbers).
     */
    @Query("""
            SELECT c.uuid, c.caseType, c.status, c.priority, c.priorityScore,
                   c.filingDate, c.court.courtCode, c.court.state,
                   c.isDuplicateChecked, c.isJurisdictionVerified, c.duplicateCaseUuids,
                   c.registeredAt, c.filingYear
            FROM CaseFile c
            WHERE c.isDeleted = false
            ORDER BY c.createdAt DESC
            """)
    List<Object[]> researchDatasetProjection();
}
