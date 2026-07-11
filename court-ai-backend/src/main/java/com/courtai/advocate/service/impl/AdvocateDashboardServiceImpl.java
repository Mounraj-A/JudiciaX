package com.courtai.advocate.service.impl;

import com.courtai.advocate.dto.AdvocateDashboardResponse;
import com.courtai.advocate.entity.Advocate;
import com.courtai.advocate.service.AdvocateDashboardService;
import com.courtai.advocate.util.AdvocateSecurityUtil;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.common.enums.CaseStatus;
import com.courtai.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link AdvocateDashboardService}.
 *
 * <p>Aggregates case counts, hearing stats, and notification counts
 * from existing repositories without any N+1 queries.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdvocateDashboardServiceImpl implements AdvocateDashboardService {

    private final AdvocateSecurityUtil    securityUtil;
    private final CaseFileRepository      caseFileRepository;
    private final NotificationRepository  notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public AdvocateDashboardResponse getDashboard() {
        Advocate advocate = securityUtil.getCurrentAdvocate();
        String advocateUuid = advocate.getUuid();
        String userUuid     = advocate.getUser().getUuid();

        // Case counts
        long totalCases   = caseFileRepository.findByAdvocateUuid(advocateUuid, PageRequest.of(0, 1)).getTotalElements();
        long activeCases  = caseFileRepository.findByAdvocateUuidAndStatus(advocateUuid, CaseStatus.IN_PROGRESS, PageRequest.of(0, 1)).getTotalElements();
        long pendingCases = caseFileRepository.findByAdvocateUuidAndStatus(advocateUuid, CaseStatus.FILED, PageRequest.of(0, 1)).getTotalElements();
        long closedCases  = caseFileRepository.findByAdvocateUuidAndStatus(advocateUuid, CaseStatus.DISPOSED, PageRequest.of(0, 1)).getTotalElements();

        // Unread notifications
        long unreadNotifications = notificationRepository
                .countByRecipientUuidAndIsReadFalseAndIsDeletedFalse(userUuid);

        // Profile completeness check
        boolean profileComplete = advocate.getBarCouncilNumber() != null
                && advocate.getStateBarCouncil() != null
                && advocate.getOfficeCity() != null;

        boolean profileVerified = Boolean.TRUE.equals(advocate.getIsVerified());

        String profileMessage = profileComplete
                ? (profileVerified ? "Profile is complete and verified." : "Profile complete — awaiting admin verification.")
                : "Complete your Bar Council registration details to file cases.";

        String fullName = advocate.getUser().getFullName();
        if (fullName == null || fullName.isBlank()) {
            fullName = advocate.getUser().getEmail();
        }

        return AdvocateDashboardResponse.builder()
                .advocateName(fullName)
                .barCouncilNumber(advocate.getBarCouncilNumber())
                .profileComplete(profileComplete)
                .profileVerified(profileVerified)
                .totalCases(totalCases)
                .activeCases(activeCases)
                .pendingCases(pendingCases)
                .closedCases(closedCases)
                .hearingsTodayCount(0L)           // Phase 3: add hearing date filter
                .unreadNotificationsCount(unreadNotifications)
                .profileCompletionMessage(profileMessage)
                .build();
    }
}
