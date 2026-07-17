package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI recommendation and explainability analytics response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIAnalyticsResponse {

    // ── Summary ───────────────────────────────────────────────────────────

    private long totalCasesAnalysed;
    private long casesWithAnalysis;
    private long casesWithoutAnalysis;

    // ── Score Averages ────────────────────────────────────────────────────

    private Double avgConfidenceScore;
    private Double avgUrgencyScore;
    private Double avgDelayImpactScore;
    private Double avgTrustScore;

    // ── Queue Status ──────────────────────────────────────────────────────

    private long queuedCases;
    private long pendingInQueue;
    private long processingInQueue;
    private long completedInQueue;
    private long failedInQueue;

    // ── Model Information ─────────────────────────────────────────────────

    /** Distribution of model versions used. */
    private List<GraphDataPoint> modelVersionDistribution;

    // ── Distributions ─────────────────────────────────────────────────────

    /** Confidence score buckets: 0-20, 20-40, 40-60, 60-80, 80-100. */
    private List<GraphDataPoint> confidenceDistribution;

    /** Urgency score buckets. */
    private List<GraphDataPoint> urgencyDistribution;

    /** Delay impact score buckets. */
    private List<GraphDataPoint> delayImpactDistribution;

    // ── Trends ────────────────────────────────────────────────────────────

    /** Monthly AI analysis throughput (cases analysed per month). */
    private List<TimeSeriesDataPoint> aiThroughputTrend;

    /** Monthly average urgency score trend. */
    private List<TimeSeriesDataPoint> urgencyScoreTrend;

    /** Monthly average confidence score trend. */
    private List<TimeSeriesDataPoint> confidenceScoreTrend;
}
