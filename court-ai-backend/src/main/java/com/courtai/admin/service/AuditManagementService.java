package com.courtai.admin.service;

import com.courtai.admin.dto.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/** Admin service for audit log search, filter, and export. */
public interface AuditManagementService {
    Page<AuditLogResponse> searchAuditLogs(String action, String actorUuid,
                                            String entityType, String outcome,
                                            Pageable pageable);
    AuditLogResponse getAuditLogByUuid(String uuid);
    List<AuditLogResponse> exportByDateRange(LocalDateTime from, LocalDateTime to);
}
