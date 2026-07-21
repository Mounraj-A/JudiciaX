package com.courtai.ai.ocr.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class OCRValidationResponse {
    private Boolean isValid;
    private Integer expectedPages;
    private String expectedLanguage;
    private Double expectedConfidence;
    private List<Integer> missingPages;
    private List<Integer> unreadablePages;
    private Boolean pageOrderCorrect;
    private Boolean documentComplete;
    private String message;
}
