package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Case Trust Score (CTS) analytics response.
 *
 * <p>Measures trustworthiness of submitted case documentation and evidence.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrustAnalyticsResponse {

    // ── Summary Metrics ───────────────────────────────────────────────────

    private long totalCasesWithTrustScore;
    private Double avgTrustScore;
    private Double maxTrustScore;
    private Double minTrustScore;

    // ── Trust Distribution ────────────────────────────────────────────────

    /** Trust score buckets: 0-20, 20-40, 40-60, 60-80, 80-100. */
    private List<GraphDataPoint> trustDistribution;

    private long lowTrustCaseCount;    // score < 40
    private long highTrustCaseCount;   // score >= 80
    private long mediumTrustCaseCount; // 40-80

    // ── Impact Analysis ───────────────────────────────────────────────────

    /** Avg trust score for cases with all documents verified vs unverified. */
    private Double avgTrustVerifiedDocs;
    private Double avgTrustUnverifiedDocs;

    /** Avg trust for cases with evidence vs no evidence. */
    private Double avgTrustWithEvidence;
    private Double avgTrustWithoutEvidence;

    /** Avg trust for duplicate-flagged vs clean cases. */
    private Double avgTrustDuplicateFlagged;
    private Double avgTrustClean;

    /** Avg trust for jurisdiction-verified vs unverified. */
    private Double avgTrustJurisdictionVerified;
    private Double avgTrustJurisdictionUnverified;

    // ── Low Trust Cases ───────────────────────────────────────────────────

    /** Top 10 lowest trust score pending cases. */
    private List<CaseReportResponse> lowTrustCases;

    // ── Trend ─────────────────────────────────────────────────────────────

    /** Monthly average trust score trend. */
    private List<TimeSeriesDataPoint> trustScoreTrend;
}
