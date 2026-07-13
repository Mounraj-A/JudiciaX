package com.courtai.admin.service.impl;

import com.courtai.admin.dto.SecurityEventResponse;
import com.courtai.admin.repository.LoginSecurityEventRepository;
import com.courtai.admin.service.SecurityMonitoringService;
import com.courtai.audit.service.AuditService;
import com.courtai.auth.repository.UserSessionRepository;
import com.courtai.auth.service.UserSessionService;
import com.courtai.common.enums.CaseStatus;
import com.courtai.common.enums.SessionStatus;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.user.entity.User;
import com.courtai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SecurityMonitoringServiceImpl implements SecurityMonitoringService {

    private final LoginSecurityEventRepository securityEventRepo;
    private final UserSessionRepository        userSessionRepository;
    private final UserRepository               userRepository;
    private final UserSessionService           userSessionService;
    private final AuditService                 auditService;

    @Override
    public Page<SecurityEventResponse> getSecurityEvents(Pageable pageable) {
        return securityEventRepo.findByIsDeletedFalseOrderByCreatedAtDesc(pageable)
                .map(e -> SecurityEventResponse.builder()
                        .uuid(e.getUuid())
                        .eventType(e.getEventType())
                        .userUuid(e.getUser() != null ? e.getUser().getUuid() : null)
                        .userEmail(e.getUser() != null ? e.getUser().getEmail() : null)
                        .ipAddress(e.getIpAddress())
                        .browser(e.getBrowser())
                        .device(e.getDevice())
                        .status(e.getStatus())
                        .details(e.getDetails())
                        .eventTime(e.getEventTime())
                        .createdAt(e.getCreatedAt())
                        .build());
    }

    @Override
    public Page<SecurityEventResponse> getSecurityEventsByType(String eventType, Pageable pageable) {
        return securityEventRepo.findByEventTypeAndIsDeletedFalse(eventType, pageable)
                .map(e -> SecurityEventResponse.builder()
                        .uuid(e.getUuid())
                        .eventType(e.getEventType())
                        .userUuid(e.getUser() != null ? e.getUser().getUuid() : null)
                        .userEmail(e.getUser() != null ? e.getUser().getEmail() : null)
                        .ipAddress(e.getIpAddress())
                        .browser(e.getBrowser())
                        .device(e.getDevice())
                        .status(e.getStatus())
                        .details(e.getDetails())
                        .eventTime(e.getEventTime())
                        .createdAt(e.getCreatedAt())
                        .build());
    }

    @Override
    public Map<String, Object> getSecuritySummary() {
        LocalDateTime dayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime dayEnd   = LocalDateTime.now();
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("activeSessions",       userSessionRepository.countByStatus(SessionStatus.ACTIVE));
        summary.put("failedLoginsToday",    securityEventRepo.countByEventTypeAndTimeBetween(
                "FAILED_LOGIN", dayStart, dayEnd));
        summary.put("accountLocksToday",    securityEventRepo.countByEventTypeAndTimeBetween(
                "ACCOUNT_LOCKED", dayStart, dayEnd));
        summary.put("suspiciousLoginsToday",securityEventRepo.countByEventTypeAndTimeBetween(
                "SUSPICIOUS_LOGIN", dayStart, dayEnd));
        return summary;
    }

    @Override
    @Transactional
    public void revokeUserSession(String sessionUuid, String adminUuid) {
        userSessionRepository.updateStatusByUuid(sessionUuid, SessionStatus.REVOKED);
        auditService.logSuccess("SESSION_REVOKED", "UserSession", sessionUuid,
                "Session revoked by admin " + adminUuid);
    }

    @Override
    @Transactional
    public void revokeAllUserSessions(String userUuid, String adminUuid) {
        User user = userRepository.findByUuidAndIsDeletedFalse(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userUuid));
        userSessionService.revokeAllSessions(user);
        auditService.logSuccess("ALL_SESSIONS_REVOKED", "User", userUuid,
                "All sessions revoked by admin " + adminUuid);
    }
}
