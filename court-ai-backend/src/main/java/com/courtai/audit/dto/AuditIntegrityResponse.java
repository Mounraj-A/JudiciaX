package com.courtai.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditIntegrityResponse {
    private String uuid;
    private String auditEventUuid;
    private String previousHash;
    private String currentHash;
    private String verificationStatus;
}
