package com.courtai.audit.service;

import com.courtai.audit.entity.AuditEvent;

public interface AuditIntegrityService {
    void generateAndStoreHash(AuditEvent auditEvent);
    boolean verifyIntegrity(String auditEventUuid);
    boolean verifyChain();
}
