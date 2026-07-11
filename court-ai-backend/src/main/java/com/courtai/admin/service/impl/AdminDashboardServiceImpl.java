package com.courtai.admin.service.impl;

import com.courtai.admin.service.AdminDashboardService;
import com.courtai.auth.repository.LoginHistoryRepository;
import com.courtai.auth.repository.SecurityEventRepository;
import com.courtai.auth.repository.UserSessionRepository;
import com.courtai.common.enums.AccountStatus;
import com.courtai.common.enums.LoginStatus;
import com.courtai.common.enums.SessionStatus;
import com.courtai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository         userRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final UserSessionRepository  userSessionRepository;
    private final SecurityEventRepository securityEventRepository;

    @Override
    public Map<String, Object> getDashboardStats() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay   = LocalDate.now().atTime(LocalTime.MAX);

        Map<String, Object> stats = new LinkedHashMap<>();

        // User counts
        stats.put("totalUsers",          userRepository.findAllByIsDeletedFalse().size());
        stats.put("pendingApprovals",     userRepository.countByAccountStatusAndIsDeletedFalse(AccountStatus.PENDING_VERIFICATION));
        stats.put("lockedAccounts",       userRepository.countByAccountStatusAndIsDeletedFalse(AccountStatus.LOCKED));
        stats.put("newRegistrationsToday", userRepository.countByCreatedAtBetween(startOfDay, endOfDay));

        // Login stats
        stats.put("loginsTodayTotal",  loginHistoryRepository.countByLoginTimeBetween(startOfDay, endOfDay));
        stats.put("loginsTodayFailed", loginHistoryRepository.countByStatusAndLoginTimeBetween(LoginStatus.FAILED, startOfDay, endOfDay));
        stats.put("loginsTodaySuccess", loginHistoryRepository.countByStatusAndLoginTimeBetween(LoginStatus.SUCCESS, startOfDay, endOfDay));

        // Session stats
        stats.put("activeSessionsNow", userSessionRepository.countByStatus(SessionStatus.ACTIVE));

        // Security events
        stats.put("highSeverityEventsLast24h",
                securityEventRepository.countHighSeverityEventsSince("HIGH",
                        LocalDateTime.now().minusHours(24)));
        stats.put("recentSecurityEvents",
                securityEventRepository.findAllByOrderByEventTimeDesc(PageRequest.of(0, 20)));

        log.debug("Dashboard stats computed");
        return stats;
    }
}
