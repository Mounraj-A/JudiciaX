package com.courtai.reports.mapper;

import com.courtai.reports.dto.response.DashboardSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for assembling dashboard summary sub-sections from raw counts.
 *
 * <p>Methods here use the builder pattern directly since dashboard data
 * is assembled from multiple repositories, not mapped 1:1 from a single entity.</p>
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DashboardMapper {

    public DashboardSummaryResponse buildCourtDashboard(
            long totalCourts, long totalBenches, long totalJudges, long totalClerks,
            long totalAdvocates, long totalRegistered, long totalPending, long totalDisposed,
            long todaysHearings, long todaysOrders,
            Double avgDisposalDays, Double avgRegistrationDays, Double avgHearingDelayDays) {

        return DashboardSummaryResponse.builder()
                .courtStats(DashboardSummaryResponse.CourtStats.builder()
                        .totalCourts(totalCourts)
                        .totalBenches(totalBenches)
                        .totalJudges(totalJudges)
                        .totalClerks(totalClerks)
                        .totalAdvocates(totalAdvocates)
                        .totalRegisteredCases(totalRegistered)
                        .totalPendingCases(totalPending)
                        .totalDisposedCases(totalDisposed)
                        .todaysHearings(todaysHearings)
                        .todaysOrders(todaysOrders)
                        .avgDisposalDays(avgDisposalDays)
                        .avgRegistrationDays(avgRegistrationDays)
                        .avgHearingDelayDays(avgHearingDelayDays)
                        .build())
                .build();
    }

    public DashboardSummaryResponse buildJudgeDashboard(
            long assignedCases, long pendingCases, long disposedCases, long reservedJudgments,
            long todaysHearings, Double avgDisposalDays, Double avgHearingsPerDay,
            Double avgAdjournments, Double aiAcceptanceRate, long casesWithAi, Double performanceScore) {

        return DashboardSummaryResponse.builder()
                .judgeStats(DashboardSummaryResponse.JudgeStats.builder()
                        .assignedCases(assignedCases)
                        .pendingCases(pendingCases)
                        .disposedCases(disposedCases)
                        .reservedJudgments(reservedJudgments)
                        .todaysHearings(todaysHearings)
                        .avgDisposalDays(avgDisposalDays)
                        .avgHearingsPerDay(avgHearingsPerDay)
                        .avgAdjournments(avgAdjournments)
                        .aiAcceptanceRate(aiAcceptanceRate)
                        .casesWithAiAnalysis(casesWithAi)
                        .performanceScore(performanceScore)
                        .build())
                .build();
    }

    public DashboardSummaryResponse buildAdvocateDashboard(
            long filedCases, long pendingCases, long disposedCases, long returnedCases,
            long rejectedCases, Double successRate, Double avgFilingDays,
            Double documentCompletionRate, Double evidenceCompletionRate) {

        return DashboardSummaryResponse.builder()
                .advocateStats(DashboardSummaryResponse.AdvocateStats.builder()
                        .filedCases(filedCases)
                        .pendingCases(pendingCases)
                        .disposedCases(disposedCases)
                        .returnedCases(returnedCases)
                        .rejectedCases(rejectedCases)
                        .successRate(successRate)
                        .avgFilingDays(avgFilingDays)
                        .documentCompletionRate(documentCompletionRate)
                        .evidenceCompletionRate(evidenceCompletionRate)
                        .build())
                .build();
    }

    public DashboardSummaryResponse buildClerkDashboard(
            long scrutiny, long registered, long returned, long rejected,
            Double avgScrutinyHours, long duplicateCount, long pendingVerification, Double accuracy) {

        return DashboardSummaryResponse.builder()
                .clerkStats(DashboardSummaryResponse.ClerkStats.builder()
                        .casesUnderScrutiny(scrutiny)
                        .registeredCases(registered)
                        .returnedCases(returned)
                        .rejectedCases(rejected)
                        .avgScrutinyHours(avgScrutinyHours)
                        .duplicateDetectionCount(duplicateCount)
                        .pendingVerification(pendingVerification)
                        .verificationAccuracy(accuracy)
                        .build())
                .build();
    }

    public DashboardSummaryResponse buildAdminDashboard(
            long totalUsers, long activeUsers, long totalCourts,
            long totalDocuments, long totalDocSizeBytes,
            long auditLogs, long securityEvents, long aiAnalyses, long notificationsSent) {

        return DashboardSummaryResponse.builder()
                .adminStats(DashboardSummaryResponse.AdminStats.builder()
                        .totalUsers(totalUsers)
                        .activeUsers(activeUsers)
                        .totalCourts(totalCourts)
                        .totalDocuments(totalDocuments)
                        .totalDocumentSizeBytes(totalDocSizeBytes)
                        .auditLogCount(auditLogs)
                        .securityEventCount(securityEvents)
                        .aiAnalysisCount(aiAnalyses)
                        .notificationsSent(notificationsSent)
                        .build())
                .build();
    }
}
