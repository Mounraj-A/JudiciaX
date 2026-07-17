package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Court-level analytics and performance report response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourtReportResponse {

    private Long courtId;
    private String courtUuid;
    private String courtName;
    private String courtCode;
    private String courtType;
    private String state;
    private String district;
    private Boolean isActive;

    // ── Case Counts ───────────────────────────────────────────────────────

    private long totalCases;
    private long pendingCases;
    private long disposedCases;
    private long filedToday;
    private long registeredToday;

    // ── Infrastructure ────────────────────────────────────────────────────

    private long totalBenches;
    private long totalRooms;
    private long totalJudges;
    private long totalClerks;
    private long totalAdvocates;

    // ── Timing Metrics ────────────────────────────────────────────────────

    /** Average number of days from filing to disposal. */
    private Double avgDisposalDays;

    /** Average number of days from filing to registration. */
    private Double avgRegistrationDays;

    /** Average hearing delay in days (scheduled vs actual). */
    private Double avgHearingDelayDays;

    // ── AI Metrics ────────────────────────────────────────────────────────

    private long casesAnalysedByAI;
    private Double avgPriorityScore;
    private Double avgTrustScore;

    // ── Breakdown Charts ──────────────────────────────────────────────────

    /** Case counts grouped by CaseType. */
    private List<GraphDataPoint> casesByType;

    /** Case counts grouped by CaseStatus. */
    private List<GraphDataPoint> casesByStatus;

    /** Case counts grouped by CasePriority. */
    private List<GraphDataPoint> casesByPriority;

    /** Monthly filing trend for the current year. */
    private List<TimeSeriesDataPoint> monthlyFilingTrend;
}
