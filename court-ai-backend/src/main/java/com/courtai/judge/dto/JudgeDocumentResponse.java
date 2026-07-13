package com.courtai.judge.dto;

import com.courtai.common.enums.DocumentType;
import com.courtai.common.enums.OcrStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Read-only document metadata response for the judge.
 * No binary file content is exposed — only metadata and storage reference.
 */
@Getter
@Builder
public class JudgeDocumentResponse {

    private String uuid;
    private String fileName;
    private String originalFileName;
    private DocumentType documentType;
    private String mimeType;
    private Long fileSizeBytes;
    private Integer pageCount;
    private String description;
    private Integer version;
    private OcrStatus ocrStatus;
    private Boolean isVerified;
    private String verificationRemarks;
    private Boolean isConfidential;
    private String uploadedByUuid;
    private LocalDateTime createdAt;
    private LocalDateTime verifiedAt;
}
