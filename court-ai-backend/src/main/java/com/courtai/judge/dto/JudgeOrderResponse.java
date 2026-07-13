package com.courtai.judge.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for a judicial order or judgment.
 */
@Getter
@Builder
public class JudgeOrderResponse {

    private String uuid;
    private String caseUuid;
    private String caseNumber;
    private String orderType;
    private String title;
    private String orderText;
    private LocalDate orderDate;
    private String originalFileName;
    private String mimeType;
    private String storagePath;
    private Long fileSizeBytes;
    private Boolean isSigned;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
