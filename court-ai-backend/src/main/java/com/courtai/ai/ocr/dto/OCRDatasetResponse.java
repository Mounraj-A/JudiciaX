package com.courtai.ai.ocr.dto;

import com.courtai.ai.ocr.model.OCRDocumentType;
import com.courtai.ai.ocr.model.OCREngineType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OCRDatasetResponse {
    private String datasetId;
    private String documentUuid;
    private OCRDocumentType documentType;
    private String language;
    private OCREngineType expectedEngine;
    private Double qualityScore;
    private Double validationScore;
    private Integer retryCount;
    private Boolean manualReviewRequired;
    private Long processingTimeMs;
}
