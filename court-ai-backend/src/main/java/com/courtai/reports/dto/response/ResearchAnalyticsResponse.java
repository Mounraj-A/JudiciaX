package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Research analytics summary response.
 *
 * <p>Provides aggregate statistics over the research dataset.
 * This is the primary output used for the research paper evaluations.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResearchAnalyticsResponse {

    // ── Dataset Summary ───────────────────────────────────────────────────

    private long totalDatasetRows;
    private long casesWithAiAnalysis;
    private long casesWithDocuments;
    private long casesWithEvidence;

    // ── Score Averages ────────────────────────────────────────────────────

    private Double avgPriorityScore;
    private Double avgTrustScore;
    private Double avgUrgencyScore;
    private Double avgDelayImpactScore;
    private Double avgConfidenceScore;

    // ── Delay / Age ───────────────────────────────────────────────────────

    private Double avgAgeInDays;
    private Double avgHearingCount;

    // ── Completeness ─────────────────────────────────────────────────────

    /** Average ratio of verified documents to total documents (0–1). */
    private Double avgDocumentCompletenessRate;

    /** Average ratio of verified evidence to total evidence (0–1). */
    private Double avgEvidenceCompletenessRate;

    // ── Distributions ─────────────────────────────────────────────────────

    /** Priority score distribution by bucket. */
    private List<GraphDataPoint> priorityScoreDistribution;

    /** Trust score distribution by bucket. */
    private List<GraphDataPoint> trustScoreDistribution;

    /** Evidence completeness distribution by bucket. */
    private List<GraphDataPoint> evidenceCompletenessDistribution;

    /** Duplicate risk distribution. */
    private List<GraphDataPoint> duplicateRiskDistribution;

    // ── Explainability ────────────────────────────────────────────────────

    /** Confidence score distribution. */
    private List<GraphDataPoint> confidenceDistribution;

    /** Urgency score distribution. */
    private List<GraphDataPoint> urgencyDistribution;

    // ── Trends ────────────────────────────────────────────────────────────

    /** Monthly average priority score trend. */
    private List<TimeSeriesDataPoint> priorityTrend;

    /** Monthly average trust score trend. */
    private List<TimeSeriesDataPoint> trustTrend;
}
