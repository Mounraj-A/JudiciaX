package com.courtai.advocate.dto;

import com.courtai.common.enums.EvidenceType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Evidence item response for the Advocate Portal.
 */
@Getter
@Builder
public class EvidenceResponse {

    private String uuid;
    private EvidenceType evidenceType;
    private String title;
    private String description;
    private String collectedBy;
    private LocalDate collectedAt;
    private String location;
    private Boolean isAdmitted;
    private String admissionRemarks;
    /** UUID of the linked document (if any). */
    private String documentUuid;
    private String documentFileName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
