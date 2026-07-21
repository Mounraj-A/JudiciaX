package com.courtai.ai.ocr.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OCRWorkflowResponse {
    private String documentUuid;
    private String status; // ACCEPTED, RETRY, MANUAL_REVIEW, REJECTED
    private String message;
    private OCRProfileResponse profile;
    private OCRDecisionResponse decision;
    private OCRValidationResponse validation;
}
