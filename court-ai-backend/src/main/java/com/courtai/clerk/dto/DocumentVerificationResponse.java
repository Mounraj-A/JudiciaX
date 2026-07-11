package com.courtai.clerk.dto;

import com.courtai.common.enums.DocumentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/** Document detail for clerk verification view. */
@Data
@Builder
public class DocumentVerificationResponse {
    private String uuid;
    private String caseUuid;
    private String caseNumber;
    private String originalFileName;
    private DocumentType documentType;
    private String mimeType;
    private Long fileSizeBytes;
    private String description;
    private Boolean isVerified;
    private String verificationRemarks;
    private String verifiedByUuid;
    private LocalDateTime verifiedAt;
    private String rejectionReason;
    private String uploadedByUuid;
    private LocalDateTime createdAt;
}
