package com.courtai.admin.service.impl;

import com.courtai.admin.dto.AuditLogResponse;
import com.courtai.admin.service.AuditManagementService;
import com.courtai.audit.entity.AuditLog;
import com.courtai.audit.repository.AuditLogRepository;
import com.courtai.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditManagementServiceImpl implements AuditManagementService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public Page<AuditLogResponse> searchAuditLogs(String action, String actorUuid,
                                                   String entityType, String outcome,
                                                   Pageable pageable) {
        // Use action-based query if action is provided; otherwise retrieve all
        if (action != null && !action.isBlank()) {
            return auditLogRepository.findByActionOrderByCreatedAtDesc(action, pageable)
                    .map(this::toResponse);
        }
        if (actorUuid != null && !actorUuid.isBlank()) {
            return auditLogRepository.findByActorUuidOrderByCreatedAtDesc(actorUuid, pageable)
                    .map(this::toResponse);
        }
        if (entityType != null && !entityType.isBlank()) {
            return auditLogRepository.findByEntityTypeAndEntityUuidOrderByCreatedAtDesc(
                    entityType, null, pageable).map(this::toResponse);
        }
        return auditLogRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public AuditLogResponse getAuditLogByUuid(String uuid) {
        return auditLogRepository.findAll().stream()
                .filter(a -> uuid.equals(a.getUuid()))
                .findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("AuditLog", "uuid", uuid));
    }

    @Override
    public List<AuditLogResponse> exportByDateRange(LocalDateTime from, LocalDateTime to) {
        return auditLogRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(from, to)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .uuid(log.getUuid())
                .actorUuid(log.getActorUuid())
                .actorEmail(log.getActorEmail())
                .actorRole(log.getActorRole())
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityUuid(log.getEntityUuid())
                .description(log.getDescription())
                .ipAddress(log.getIpAddress())
                .requestId(log.getRequestId())
                .outcome(log.getOutcome())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
