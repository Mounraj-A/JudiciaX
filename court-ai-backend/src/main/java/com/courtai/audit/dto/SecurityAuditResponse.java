package com.courtai.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityAuditResponse {
    private String uuid;
    private String correlationId;
    private String eventType;
    private String ipAddress;
    private String details;
}
