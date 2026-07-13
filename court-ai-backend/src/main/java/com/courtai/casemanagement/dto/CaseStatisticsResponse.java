package com.courtai.casemanagement.dto;

import lombok.Builder;
import lombok.Getter;

/** Comprehensive case statistics response for admin dashboards and reports. */
@Getter
@Builder
public class CaseStatisticsResponse {
    // ── Global Totals ─────────────────────────────────────────────────────────
    private long totalCases;
    private long pendingCases;
    private long disposedCases;
    private long closedCases;
    private long archivedCases;
    private long cancelledCases;
    private long transferredCases;
    private long aiPendingCases;
    private long highPriorityCases;
    private long criticalCases;

    // ── Per-Stage Counts ──────────────────────────────────────────────────────
    private long draftCount;
    private long submittedCount;
    private long underScrutinyCount;
    private long registeredCount;
    private long judgeAssignedCount;
    private long hearingScheduledCount;
    private long adjournedCount;
    private long judgmentReservedCount;

    // ── Performance Metrics ───────────────────────────────────────────────────
    /** Average days from filing to disposal. */
    private Double avgDisposalDays;
    /** Average hearings per disposed case. */
    private Double avgHearingsPerCase;
    /** Average delay between hearing schedule and actual hearing (days). */
    private Double avgHearingDelayDays;

    // ── Metadata ──────────────────────────────────────────────────────────────
    private String reportedForCourtUuid;
    private String reportedForJudgeUuid;
    private String reportedForAdvocateUuid;
    private String periodLabel;
}
