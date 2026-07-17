package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Judicial Priority Index (JPI) analytics response.
 *
 * <p>Covers priority score distribution, trends, and breakdown by category/court/judge.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriorityAnalyticsResponse {

    // ── Summary Metrics ───────────────────────────────────────────────────

    private long totalCasesWithPriorityScore;
    private Double avgPriorityScore;
    private Double maxPriorityScore;
    private Double minPriorityScore;

    // ── Priority Distribution ─────────────────────────────────────────────

    private long criticalCount;
    private long highCount;
    private long mediumCount;
    private long lowCount;

    /** Priority label → case count. */
    private List<GraphDataPoint> priorityDistribution;

    // ── Dimensional Breakdown ─────────────────────────────────────────────

    /** Avg priority score by case category. */
    private List<GraphDataPoint> priorityByCategory;

    /** Avg priority score by court. */
    private List<GraphDataPoint> priorityByCourt;

    /** Avg priority score by judge. */
    private List<GraphDataPoint> priorityByJudge;

    // ── Trends ────────────────────────────────────────────────────────────

    /** Monthly average priority score trend. */
    private List<TimeSeriesDataPoint> priorityScoreTrend;

    // ── Top Cases ─────────────────────────────────────────────────────────

    /** Top 10 highest priority pending cases. */
    private List<CaseReportResponse> highPriorityCases;
}
