package com.courtai.judge.dto;

import com.courtai.common.enums.EvidenceType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Read-only evidence response for the judge.
 */
@Getter
@Builder
public class JudgeEvidenceResponse {

    private String uuid;
    private EvidenceType evidenceType;
    private String title;
    private String description;
    private String documentUuid;    // linked document UUID if any
    private LocalDate collectedAt;
    private String collectedBy;
    private String location;
    private Boolean isAdmitted;
    private String admissionRemarks;
    private Boolean isVerified;
    private String verificationRemarks;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
}
