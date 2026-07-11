package com.courtai.advocate.dto;

import com.courtai.common.enums.DocumentType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Document metadata response for the Advocate Portal.
 * Never exposes internal IDs — only UUIDs and metadata.
 */
@Getter
@Builder
public class DocumentResponse {

    private String uuid;
    private String originalFileName;
    private DocumentType documentType;
    private String mimeType;
    private Long fileSizeBytes;
    private String description;
    private Integer version;
    private Boolean isVerified;
    private Boolean isConfidential;
    private String uploadedByUuid;
    private LocalDateTime createdAt;
}
