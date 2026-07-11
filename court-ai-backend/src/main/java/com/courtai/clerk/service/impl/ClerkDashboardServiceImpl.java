package com.courtai.clerk.service.impl;

import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.clerk.dto.ClerkDashboardResponse;
import com.courtai.clerk.entity.Clerk;
import com.courtai.clerk.repository.CaseObjectionRepository;
import com.courtai.clerk.service.ClerkDashboardService;
import com.courtai.clerk.util.ClerkSecurityUtil;
import com.courtai.common.enums.CaseStatus;
import com.courtai.document.repository.DocumentRepository;
import com.courtai.evidence.repository.EvidenceRepository;
import com.courtai.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/** Implementation of {@link ClerkDashboardService}. */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClerkDashboardServiceImpl implements ClerkDashboardService {

    private final ClerkSecurityUtil       securityUtil;
    private final CaseFileRepository      caseFileRepository;
    private final DocumentRepository      documentRepository;
    private final EvidenceRepository      evidenceRepository;
    private final NotificationRepository  notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public ClerkDashboardResponse getDashboard() {
        Clerk clerk = securityUtil.getCurrentClerk();
        Long courtId = clerk.getCourt() != null ? clerk.getCourt().getId() : null;
        String userUuid = clerk.getUser().getUuid();

        // Case counts
        long pendingScrutiny  = courtId == null ? 0 :
            caseFileRepository.findByStatusInAndCourtIdAndIsDeletedFalse(
                    List.of(CaseStatus.SUBMITTED), courtId, PageRequest.of(0, 1)).getTotalElements();
        long underScrutiny    = courtId == null ? 0 :
            caseFileRepository.findByStatusInAndCourtIdAndIsDeletedFalse(
                    List.of(CaseStatus.UNDER_SCRUTINY), courtId, PageRequest.of(0, 1)).getTotalElements();
        long returnedCases    = courtId == null ? 0 :
            caseFileRepository.findByStatusInAndCourtIdAndIsDeletedFalse(
                    List.of(CaseStatus.RETURNED), courtId, PageRequest.of(0, 1)).getTotalElements();
        long registeredToday  = courtId == null ? 0 :
            caseFileRepository.countByCourtIdAndStatusAndRegisteredAtDate(
                    courtId, CaseStatus.REGISTERED, LocalDate.now());
        long pendingJudgeQ    = courtId == null ? 0 :
            caseFileRepository.countPendingJudgeAssignment(courtId);

        // Unread notifications
        long unread = notificationRepository.countByRecipientUuidAndIsReadFalseAndIsDeletedFalse(userUuid);

        // Pending document verification count (unverified docs in cases under scrutiny)
        long pendingDocVerif = 0;
        long pendingEvidVerif = 0;
        if (courtId != null) {
            var underScrutinyCases = caseFileRepository.findByStatusInAndCourtIdAndIsDeletedFalse(
                    List.of(CaseStatus.UNDER_SCRUTINY), courtId, PageRequest.of(0, 1000));
            for (var c : underScrutinyCases.getContent()) {
                pendingDocVerif += documentRepository.findByCaseFileUuidAndIsDeletedFalse(c.getUuid())
                        .stream().filter(d -> !Boolean.TRUE.equals(d.getIsVerified())).count();
                pendingEvidVerif += evidenceRepository.findByCaseFileUuidAndIsDeletedFalse(c.getUuid())
                        .stream().filter(e -> !Boolean.TRUE.equals(e.getIsVerified())).count();
            }
        }

        String fullName = clerk.getUser().getFullName();
        String courtName = clerk.getCourt() != null ? clerk.getCourt().getCourtName() : "Unassigned";

        return ClerkDashboardResponse.builder()
                .clerkName(fullName)
                .courtName(courtName)
                .employeeId(clerk.getEmployeeId())
                .pendingScrutinyCount(pendingScrutiny)
                .underScrutinyCount(underScrutiny)
                .returnedCasesCount(returnedCases)
                .registeredTodayCount(registeredToday)
                .pendingDocumentVerificationCount(pendingDocVerif)
                .pendingEvidenceVerificationCount(pendingEvidVerif)
                .duplicateAlertCount(0L)   // Phase 3: query cases with duplicateCaseUuids != null
                .pendingJudgeAssignmentCount(pendingJudgeQ)
                .unreadNotificationsCount(unread)
                .welcomeMessage("Welcome, " + fullName + ". You have " + pendingScrutiny
                        + " case(s) awaiting scrutiny in " + courtName + ".")
                .build();
    }
}
