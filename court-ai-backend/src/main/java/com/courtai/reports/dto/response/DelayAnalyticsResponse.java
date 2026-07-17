package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Delay and backlog analytics response.
 *
 * <p>Measures how delayed cases are from their expected timelines.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DelayAnalyticsResponse {

    // ── Summary ───────────────────────────────────────────────────────────

    private long totalCasesAnalysed;
    private Double avgDelayDays;
    private Double maxDelayDays;
    private Double medianDelayDays;
    private long backlogCount;  // cases pending > 90 days

    // ── Distribution ──────────────────────────────────────────────────────

    /** Delay buckets: <30d, 30-90d, 90-180d, 180-365d, >365d. */
    private List<GraphDataPoint> delayDistribution;

    // ── Dimensional Breakdown ─────────────────────────────────────────────

    /** Avg delay by case status. */
    private List<GraphDataPoint> delayByStatus;

    /** Avg delay by case type. */
    private List<GraphDataPoint> delayByType;

    /** Avg delay by court (top 10 worst). */
    private List<GraphDataPoint> delayByCourt;

    /** Avg delay by judge (top 10 worst). */
    private List<GraphDataPoint> delayByJudge;

    // ── Adjournment Reasons ───────────────────────────────────────────────

    /** Most frequent adjournment reasons from Hearing.adjournReason. */
    private List<GraphDataPoint> adjournmentReasons;

    // ── Backlog Trend ─────────────────────────────────────────────────────

    /** Monthly backlog count trend. */
    private List<TimeSeriesDataPoint> backlogTrend;
}
