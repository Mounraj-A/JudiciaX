package com.courtai.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditSearchRequest {
    private String caseNumber;
    private String user;
    private String role;
    private String action;
    private String module;
    private String entity;
    private String uuid;
    private String court;
    private String judge;
    private String advocate;
    private String ipAddress;
    private String browser;
    private String device;
    private String correlationId;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String keyword;
}
