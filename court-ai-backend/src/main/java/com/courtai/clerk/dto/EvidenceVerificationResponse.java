package com.courtai.clerk.dto;

import com.courtai.common.enums.EvidenceType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Evidence detail for clerk verification view. */
@Data
@Builder
public class EvidenceVerificationResponse {
    private String uuid;
    private String caseUuid;
    private String caseNumber;
    private EvidenceType evidenceType;
    private String title;
    private String description;
    private String collectedBy;
    private LocalDate collectedAt;
    private String location;
    private Boolean isAdmitted;
    private Boolean isVerified;
    private String verifiedByUuid;
    private LocalDateTime verifiedAt;
    private String verificationRemarks;
    private String rejectionReason;
    private String linkedDocumentUuid;
    private LocalDateTime createdAt;
}
