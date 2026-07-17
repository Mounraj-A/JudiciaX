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
public class AuditSummaryResponse {
    private String uuid;
    private String action;
    private String module;
    private String actorName;
    private LocalDateTime timestamp;
    private String status;
}
