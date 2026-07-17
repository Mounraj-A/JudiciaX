package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Advocate performance analytics report response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvocateReportResponse {

    private String advocateUuid;
    private String advocateName;
    private String barCouncilNumber;
    private String stateBarCouncil;
    private String specialization;
    private String lawFirm;
    private String verificationStatus;
    private Integer yearsOfPractice;

    // ── Case Counts ───────────────────────────────────────────────────────

    private long filedCases;
    private long pendingCases;
    private long disposedCases;
    private long returnedCases;
    private long rejectedCases;
    private long activeCases;

    // ── Rate Metrics ──────────────────────────────────────────────────────

    /** Percentage of cases reaching DISPOSED status. */
    private Double successRate;

    /** Average days from advocate assignment to case registration. */
    private Double avgFilingDays;

    // ── Document & Evidence Completion ────────────────────────────────────

    /** Percentage of filed cases with all documents verified. */
    private Double documentCompletionRate;

    /** Percentage of filed cases with at least one evidence item. */
    private Double evidenceCompletionRate;

    // ── Charts ────────────────────────────────────────────────────────────

    /** Case counts grouped by status. */
    private List<GraphDataPoint> casesByStatus;

    /** Monthly filing trend. */
    private List<TimeSeriesDataPoint> monthlyFilingTrend;
}
