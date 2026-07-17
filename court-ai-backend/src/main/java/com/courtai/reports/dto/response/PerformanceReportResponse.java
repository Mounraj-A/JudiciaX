package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * System-wide performance report response for a given period.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReportResponse {

    /** Report period label: e.g., "2025", "Q1-2025", "January 2025". */
    private String period;

    /** Period type: ANNUAL, QUARTERLY, MONTHLY. */
    private String periodType;

    // ── Case Performance ──────────────────────────────────────────────────

    private long totalCasesFiled;
    private long totalCasesRegistered;
    private long totalCasesDisposed;
    private long totalCasesPending;

    /** Disposal rate: disposedCases / totalCasesFiled × 100. */
    private Double disposalRate;

    /** Average days from filing to disposal. */
    private Double avgDisposalDays;

    // ── Hearing Performance ───────────────────────────────────────────────

    private long totalHearingsScheduled;
    private long totalHearingsCompleted;
    private long totalHearingsAdjourned;

    /** On-time hearing rate: completed on scheduled date / total × 100. */
    private Double onTimeHearingRate;

    // ── AI Performance ────────────────────────────────────────────────────

    private long casesAnalysedByAI;

    /** AI usage rate: cases with analysis / total registered × 100. */
    private Double aiUsageRate;

    private Double avgAiConfidence;

    // ── Audit & Security ──────────────────────────────────────────────────

    private long auditLogCount;
    private long securityEventCount;

    // ── Notification Performance ──────────────────────────────────────────

    private long totalNotificationsSent;
    private Double notificationDeliveryRate;

    // ── Trend Charts ──────────────────────────────────────────────────────

    private List<TimeSeriesDataPoint> filingTrend;
    private List<TimeSeriesDataPoint> disposalTrend;
    private List<TimeSeriesDataPoint> aiAnalysisTrend;
}
