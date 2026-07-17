package com.courtai.audit.service;

import com.courtai.audit.dto.AuditExportRequest;

public interface AuditExportService {
    byte[] exportAuditLogs(AuditExportRequest request);
}
