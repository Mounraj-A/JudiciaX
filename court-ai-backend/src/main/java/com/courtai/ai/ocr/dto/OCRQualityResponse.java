package com.courtai.ai.ocr.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OCRQualityResponse {
    private Integer expectedResolution;
    private Integer expectedDpi;
    private Double brightnessPlaceholder;
    private Double contrastPlaceholder;
    private Double blurPlaceholder;
    private Double rotation;
    private Double noisePlaceholder;
    private Double readinessScore;
    private String report;
}
