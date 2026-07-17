package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Judge performance analytics report response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JudgeReportResponse {

    private String judgeUuid;
    private String judgeName;
    private String judgeIdNumber;
    private String designation;
    private String specialization;
    private String courtName;
    private Integer yearsOfExperience;

    // ── Case Counts ───────────────────────────────────────────────────────

    private long assignedCases;
    private long pendingCases;
    private long disposedCases;
    private long reservedJudgments;
    private long totalOrders;

    // ── Timing Metrics ────────────────────────────────────────────────────

    /** Average days from assignment to disposal. */
    private Double avgDisposalDays;

    /** Average hearings scheduled per working day. */
    private Double avgHearingsPerDay;

    /** Average number of adjournments per case. */
    private Double avgAdjournmentsPerCase;

    // ── AI Metrics ────────────────────────────────────────────────────────

    /** Percentage of AI recommendations the judge accepted. */
    private Double aiAcceptanceRate;

    /** Number of cases where the judge reviewed AI analysis. */
    private long casesWithAiReview;

    // ── Performance Score ─────────────────────────────────────────────────

    /**
     * Composite performance score (0–100) based on:
     * disposal rate (40%) + avg disposal days (30%) + adjournment rate (20%) + AI usage (10%)
     */
    private Double performanceScore;

    // ── Charts ────────────────────────────────────────────────────────────

    /** Disposed cases by case type. */
    private List<GraphDataPoint> disposalByType;

    /** Monthly disposal trend. */
    private List<TimeSeriesDataPoint> disposalTrend;
}
