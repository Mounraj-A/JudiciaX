package com.courtai.audit.service;

import com.courtai.audit.entity.SecurityAudit;

public interface SecurityAuditService {
    void logSecurityEvent(String correlationId, String eventType, String ipAddress, String details);
}
