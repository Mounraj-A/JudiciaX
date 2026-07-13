package com.courtai.admin.service.impl;

import com.courtai.admin.dto.AdminDashboardResponse;
import com.courtai.admin.dto.UserSummaryResponse;
import com.courtai.admin.mapper.AdminUserMapper;
import com.courtai.admin.repository.MaintenanceWindowRepository;
import com.courtai.admin.repository.SystemAnnouncementRepository;
import com.courtai.admin.repository.SystemConfigurationRepository;
import com.courtai.admin.service.AdminDashboardServiceV2;
import com.courtai.auth.repository.UserSessionRepository;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.common.enums.AccountStatus;
import com.courtai.common.enums.CaseStatus;
import com.courtai.common.enums.SessionStatus;
import com.courtai.common.enums.UserRole;
import com.courtai.court.repository.CourtBenchRepository;
import com.courtai.court.repository.CourtRepository;
import com.courtai.court.repository.CourtRoomRepository;
import com.courtai.hearing.repository.HearingRepository;
import com.courtai.common.enums.HearingStatus;
import com.courtai.notification.repository.NotificationRepository;
import com.courtai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardServiceV2Impl implements AdminDashboardServiceV2 {

    private final UserRepository               userRepository;
    private final CourtRepository              courtRepository;
    private final CourtBenchRepository         courtBenchRepository;
    private final CourtRoomRepository          courtRoomRepository;
    private final CaseFileRepository           caseFileRepository;
    private final HearingRepository            hearingRepository;
    private final UserSessionRepository        userSessionRepository;
    private final NotificationRepository       notificationRepository;
    private final MaintenanceWindowRepository  maintenanceRepository;
    private final SystemAnnouncementRepository announcementRepository;
    private final SystemConfigurationRepository configRepository;
    private final AdminUserMapper              userMapper;

    @Override
    public AdminDashboardResponse getDashboard() {
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd   = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);

        // User counts
        long totalUsers    = userRepository.findAllByIsDeletedFalse().size();
        long pending       = userRepository.countByAccountStatusAndIsDeletedFalse(AccountStatus.PENDING_VERIFICATION);
        long locked        = userRepository.countByAccountStatusAndIsDeletedFalse(AccountStatus.LOCKED);
        long advocates     = userRepository.findByRoleAndIsDeletedFalse(UserRole.ROLE_ADVOCATE).size();
        long judges        = userRepository.findByRoleAndIsDeletedFalse(UserRole.ROLE_JUDGE).size();
        long clerks        = userRepository.findByRoleAndIsDeletedFalse(UserRole.ROLE_CLERK).size();
        long admins        = userRepository.findByRoleAndIsDeletedFalse(UserRole.ROLE_ADMIN).size();
        long activeUsers   = userRepository.findByAccountStatusAndIsDeletedFalse(AccountStatus.ACTIVE).size();

        // Court counts
        long courts    = courtRepository.count();
        long benches   = courtBenchRepository.count();
        long rooms     = courtRoomRepository.count();

        // Case counts
        long registeredCases = caseFileRepository.findByStatusAndIsDeletedFalse(
                CaseStatus.REGISTERED, PageRequest.of(0, 1)).getTotalElements();
        long pendingCases = caseFileRepository.findByStatusAndIsDeletedFalse(
                CaseStatus.SUBMITTED, PageRequest.of(0, 1)).getTotalElements()
                + caseFileRepository.findByStatusAndIsDeletedFalse(
                        CaseStatus.UNDER_SCRUTINY, PageRequest.of(0, 1)).getTotalElements();
        long disposedCases = caseFileRepository.findByStatusAndIsDeletedFalse(
                CaseStatus.DISPOSED, PageRequest.of(0, 1)).getTotalElements();
        long todayHearings = hearingRepository.findByScheduledAtBetweenAndIsDeletedFalse(
                todayStart, todayEnd).size();

        // System counts
        long activeSessions  = userSessionRepository.countByStatus(SessionStatus.ACTIVE);
        long maintenanceActive = maintenanceRepository.findByStatusAndIsDeletedFalse(
                "ACTIVE", PageRequest.of(0, 1)).getTotalElements();
        long activeAnnouncements = announcementRepository.findByIsActiveAndIsDeletedFalseOrderByCreatedAtDesc(
                Boolean.TRUE, PageRequest.of(0, 1)).getTotalElements();

        // AI settings
        boolean aiEnabled = configRepository.findByConfigKeyAndIsDeletedFalse("AI_ENABLED")
                .map(c -> "true".equalsIgnoreCase(c.getConfigValue())).orElse(false);
        String aiVersion = configRepository.findByConfigKeyAndIsDeletedFalse("AI_MODEL_VERSION")
                .map(c -> c.getConfigValue()).orElse("N/A");

        // Recent pending users (up to 10)
        List<UserSummaryResponse> recentPending = userRepository
                .findByAccountStatusAndIsDeletedFalse(AccountStatus.PENDING_VERIFICATION)
                .stream()
                .limit(10)
                .map(userMapper::toSummary)
                .collect(Collectors.toList());

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .pendingApprovals(pending)
                .lockedUsers(locked)
                .activeUsers(activeUsers)
                .totalAdvocates(advocates)
                .totalJudges(judges)
                .totalClerks(clerks)
                .totalAdmins(admins)
                .totalCourts(courts)
                .totalBenches(benches)
                .totalCourtRooms(rooms)
                .registeredCases(registeredCases)
                .pendingCases(pendingCases)
                .disposedCases(disposedCases)
                .todayHearings(todayHearings)
                .activeSessions(activeSessions)
                .activeMaintenanceWindows(maintenanceActive)
                .activeAnnouncements(activeAnnouncements)
                .aiEnabled(aiEnabled)
                .aiModelVersion(aiVersion)
                .aiAnalyzedCasesToday(0L)
                .recentPendingUsers(recentPending)
                .build();
    }
}
