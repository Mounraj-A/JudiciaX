package com.courtai.reports.service.impl;

import com.courtai.advocate.repository.AdvocateRepository;
import com.courtai.ai.repository.CaseAnalysisRepository;
import com.courtai.audit.repository.AuditLogRepository;
import com.courtai.audit.repository.SecurityAuditRepository;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.clerk.repository.ClerkRepository;
import com.courtai.court.repository.CourtBenchRepository;
import com.courtai.court.repository.CourtRepository;
import com.courtai.document.repository.DocumentRepository;
import com.courtai.hearing.repository.HearingRepository;
import com.courtai.judge.repository.JudgeRepository;
import com.courtai.notification.repository.NotificationRepository;
import com.courtai.reports.dto.response.DashboardSummaryResponse;
import com.courtai.reports.mapper.DashboardMapper;
import com.courtai.reports.service.DashboardAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of DashboardAnalyticsService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardAnalyticsServiceImpl implements DashboardAnalyticsService {

    private final CourtRepository courtRepository;
    private final CourtBenchRepository courtBenchRepository;
    private final JudgeRepository judgeRepository;
    private final ClerkRepository clerkRepository;
    private final AdvocateRepository advocateRepository;
    private final CaseFileRepository caseFileRepository;
    private final HearingRepository hearingRepository;
    private final CaseAnalysisRepository caseAnalysisRepository;
    private final DocumentRepository documentRepository;
    private final AuditLogRepository auditLogRepository;
    private final SecurityAuditRepository securityAuditRepository;
    private final NotificationRepository notificationRepository;
    
    private final DashboardMapper dashboardMapper;

    @Override
    public DashboardSummaryResponse getCourtDashboard() {
        log.debug("Generating court global dashboard");
        
        long totalCourts = courtRepository.countByIsActiveTrueAndIsDeletedFalse();
        long totalBenches = courtBenchRepository.count(); // Assuming soft delete
        long totalJudges = judgeRepository.count();
        long totalClerks = clerkRepository.count();
        long totalAdvocates = advocateRepository.count();
        
        long totalRegistered = caseFileRepository.count(); // All non-deleted
        long totalPending = 0; // Requires JPQL or custom repository call
        long totalDisposed = 0; // Requires JPQL or custom repository call
        long todaysHearings = 0;
        long todaysOrders = 0;
        
        Double avgDisposalDays = 0.0;
        Double avgRegistrationDays = 0.0;
        Double avgHearingDelayDays = 0.0;
        
        return dashboardMapper.buildCourtDashboard(
                totalCourts, totalBenches, totalJudges, totalClerks, totalAdvocates,
                totalRegistered, totalPending, totalDisposed, todaysHearings, todaysOrders,
                avgDisposalDays, avgRegistrationDays, avgHearingDelayDays
        );
    }

    @Override
    public DashboardSummaryResponse getJudgeDashboard(String principalUsername) {
        log.debug("Generating judge dashboard for: {}", principalUsername);
        
        long assignedCases = 0;
        long pendingCases = 0;
        long disposedCases = 0;
        long reservedJudgments = 0;
        long todaysHearings = 0;
        Double avgDisposalDays = 0.0;
        Double avgHearingsPerDay = 0.0;
        Double avgAdjournments = 0.0;
        Double aiAcceptanceRate = 0.0;
        long casesWithAi = 0;
        Double performanceScore = 0.0;
        
        return dashboardMapper.buildJudgeDashboard(
                assignedCases, pendingCases, disposedCases, reservedJudgments,
                todaysHearings, avgDisposalDays, avgHearingsPerDay, avgAdjournments,
                aiAcceptanceRate, casesWithAi, performanceScore
        );
    }

    @Override
    public DashboardSummaryResponse getAdvocateDashboard(String principalUsername) {
        log.debug("Generating advocate dashboard for: {}", principalUsername);
        
        long filedCases = 0;
        long pendingCases = 0;
        long disposedCases = 0;
        long returnedCases = 0;
        long rejectedCases = 0;
        Double successRate = 0.0;
        Double avgFilingDays = 0.0;
        Double documentCompletionRate = 0.0;
        Double evidenceCompletionRate = 0.0;
        
        return dashboardMapper.buildAdvocateDashboard(
                filedCases, pendingCases, disposedCases, returnedCases, rejectedCases,
                successRate, avgFilingDays, documentCompletionRate, evidenceCompletionRate
        );
    }

    @Override
    public DashboardSummaryResponse getClerkDashboard(String principalUsername) {
        log.debug("Generating clerk dashboard for: {}", principalUsername);
        
        long scrutiny = 0;
        long registered = 0;
        long returned = 0;
        long rejected = 0;
        Double avgScrutinyHours = 0.0;
        long duplicateCount = 0;
        long pendingVerification = 0;
        Double accuracy = 0.0;
        
        return dashboardMapper.buildClerkDashboard(
                scrutiny, registered, returned, rejected, avgScrutinyHours,
                duplicateCount, pendingVerification, accuracy
        );
    }

    @Override
    public DashboardSummaryResponse getAdminDashboard() {
        log.debug("Generating admin system dashboard");
        
        long totalUsers = 0;
        long activeUsers = 0;
        long totalCourts = courtRepository.countByIsActiveTrueAndIsDeletedFalse();
        long totalDocuments = documentRepository.count();
        long totalDocSizeBytes = 0;
        long auditLogs = auditLogRepository.count();
        long securityEvents = securityAuditRepository.count();
        long aiAnalyses = caseAnalysisRepository.count();
        long notificationsSent = notificationRepository.count();
        
        return dashboardMapper.buildAdminDashboard(
                totalUsers, activeUsers, totalCourts, totalDocuments, totalDocSizeBytes,
                auditLogs, securityEvents, aiAnalyses, notificationsSent
        );
    }
}
