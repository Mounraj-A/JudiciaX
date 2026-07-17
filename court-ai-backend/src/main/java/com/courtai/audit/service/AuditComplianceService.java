package com.courtai.audit.service;

import com.courtai.audit.dto.ComplianceAuditResponse;
import java.util.List;

public interface AuditComplianceService {
    void logComplianceViolation(String correlationId, String violationType, String details, String status);
    List<ComplianceAuditResponse> getViolations();
}
