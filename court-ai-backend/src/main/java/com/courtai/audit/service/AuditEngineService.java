package com.courtai.audit.service;

import com.courtai.audit.entity.AuditEvent;

public interface AuditEngineService {
    AuditEvent publishEvent(String module, String action, String entityType, String entityUuid, String remarks);
    AuditEvent publishEventWithCorrelation(String correlationId, String module, String action, String entityType, String entityUuid, String remarks);
}
