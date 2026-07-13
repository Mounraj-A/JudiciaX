package com.courtai.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/** Audit log entry for the admin audit search panel. */
@Getter
@Builder
public class AuditLogResponse {
    private String uuid;
    private String actorUuid;
    private String actorEmail;
    private String actorRole;
    private String action;
    private String entityType;
    private String entityUuid;
    private String description;
    private String ipAddress;
    private String requestId;
    private String outcome;
    private LocalDateTime createdAt;
}
