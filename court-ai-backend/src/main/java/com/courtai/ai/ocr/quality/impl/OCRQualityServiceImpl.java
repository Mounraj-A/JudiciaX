package com.courtai.ai.ocr.quality.impl;

import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.dto.OCRQualityResponse;
import com.courtai.ai.ocr.model.OCRDocumentType;
import com.courtai.ai.ocr.quality.OCRQualityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OCRQualityServiceImpl implements OCRQualityService {

    @Override
    public OCRQualityResponse assessQuality(OCRProfileResponse profile) {
        log.info("Assessing OCR quality readiness for document UUID: {}", profile.getDocumentUuid());

        boolean isDigital = profile.getDocumentType() == OCRDocumentType.DIGITAL_PDF;
        
        return OCRQualityResponse.builder()
                .expectedResolution(isDigital ? 0 : 300) // 300 DPI typical for scans
                .expectedDpi(isDigital ? 0 : 300)
                .brightnessPlaceholder(isDigital ? 1.0 : 0.85)
                .contrastPlaceholder(isDigital ? 1.0 : 0.90)
                .blurPlaceholder(isDigital ? 0.0 : 0.05)
                .rotation(0.0) // Assume 0 rotation for simulation
                .noisePlaceholder(isDigital ? 0.0 : 0.1)
                .readinessScore(isDigital ? 1.0 : 0.8)
                .report(isDigital ? "Perfect digital quality." : "Acceptable scanned quality. Enhancements may be applied.")
                .build();
    }
}
