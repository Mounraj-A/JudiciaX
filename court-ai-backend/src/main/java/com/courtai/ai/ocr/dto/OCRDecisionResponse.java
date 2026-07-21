package com.courtai.ai.ocr.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OCRDecisionResponse {
    private String decision; // RUN_OCR, SKIP_OCR, RETRY_OCR, MANUAL_REVIEW, FUTURE_SPEECH, FUTURE_VIDEO
    private String reason;
    private String strategyToExecute;
}
