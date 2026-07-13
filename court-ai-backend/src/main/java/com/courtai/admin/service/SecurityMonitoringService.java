package com.courtai.admin.service;

import com.courtai.admin.dto.SecurityEventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/** Admin service for security monitoring — failed logins, locks, sessions, suspicious activity. */
public interface SecurityMonitoringService {
    Page<SecurityEventResponse> getSecurityEvents(Pageable pageable);
    Page<SecurityEventResponse> getSecurityEventsByType(String eventType, Pageable pageable);
    Map<String, Object> getSecuritySummary();
    void revokeUserSession(String sessionUuid, String adminUuid);
    void revokeAllUserSessions(String userUuid, String adminUuid);
}
