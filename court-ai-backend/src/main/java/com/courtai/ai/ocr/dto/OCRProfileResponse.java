package com.courtai.ai.ocr.dto;

import com.courtai.ai.ocr.model.OCRDocumentType;
import com.courtai.ai.ocr.model.OCREngineType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OCRProfileResponse {
    private String documentUuid;
    private String caseUuid;
    private OCRDocumentType documentType;
    private String language;
    private OCREngineType expectedOcrEngine;
    private Double expectedAccuracy;
    private Long expectedProcessingTimeMs;
    private Boolean requiresRotation;
    private Boolean requiresEnhancement;
    private Boolean requiresManualReview;
    private Boolean isMultiPage;
    private Boolean isMultiColumn;
    private Boolean isHandwritten;
    private Boolean isMixedLanguage;
    private Double estimatedComplexity;
    private Boolean ocrReady;
}
