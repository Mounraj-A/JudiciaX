package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dashboard summary response aggregating key metrics across all domains.
 *
 * <p>Each inner class represents one dashboard section.
 * Roles receive only their authorised section from the dashboard endpoint.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {

    private CourtStats courtStats;
    private JudgeStats judgeStats;
    private AdvocateStats advocateStats;
    private ClerkStats clerkStats;
    private AdminStats adminStats;

    // ── Court Dashboard ───────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourtStats {
        private long totalCourts;
        private long totalBenches;
        private long totalJudges;
        private long totalClerks;
        private long totalAdvocates;
        private long totalRegisteredCases;
        private long totalPendingCases;
        private long totalDisposedCases;
        private long todaysHearings;
        private long todaysOrders;
        private Double avgDisposalDays;
        private Double avgRegistrationDays;
        private Double avgHearingDelayDays;
        private Double courtUtilizationRate;
    }

    // ── Judge Dashboard ───────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JudgeStats {
        private long assignedCases;
        private long pendingCases;
        private long disposedCases;
        private long reservedJudgments;
        private long todaysHearings;
        private Double avgDisposalDays;
        private Double avgHearingsPerDay;
        private Double avgAdjournments;
        private Double aiAcceptanceRate;
        private long casesWithAiAnalysis;
        private Double performanceScore;
    }

    // ── Advocate Dashboard ────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdvocateStats {
        private long filedCases;
        private long pendingCases;
        private long disposedCases;
        private long returnedCases;
        private long rejectedCases;
        private Double successRate;
        private Double avgFilingDays;
        private Double documentCompletionRate;
        private Double evidenceCompletionRate;
    }

    // ── Clerk Dashboard ───────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClerkStats {
        private long casesUnderScrutiny;
        private long registeredCases;
        private long returnedCases;
        private long rejectedCases;
        private Double avgScrutinyHours;
        private long duplicateDetectionCount;
        private long pendingVerification;
        private Double verificationAccuracy;
    }

    // ── Admin Dashboard ───────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminStats {
        private long totalUsers;
        private long activeUsers;
        private long totalCourts;
        private long totalDocuments;
        private long totalDocumentSizeBytes;
        private long auditLogCount;
        private long securityEventCount;
        private long aiAnalysisCount;
        private long notificationsSent;
    }
}
