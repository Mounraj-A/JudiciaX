package com.courtai.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceAuditResponse {
    private String uuid;
    private String correlationId;
    private String violationType;
    private String details;
    private String complianceStatus;
}
