package com.courtai.ai.ocr.dataset.impl;

import com.courtai.ai.ocr.dataset.OCRDatasetService;
import com.courtai.ai.ocr.dto.OCRDatasetResponse;
import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.dto.OCRValidationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class OCRDatasetServiceImpl implements OCRDatasetService {

    @Override
    public OCRDatasetResponse recordDatasetEntry(OCRProfileResponse profile, OCRValidationResponse validation, long processingTimeMs) {
        log.info("Recording OCR dataset entry for research (Metadata Only)");
        
        return OCRDatasetResponse.builder()
                .datasetId(UUID.randomUUID().toString())
                .documentUuid(profile.getDocumentUuid())
                .documentType(profile.getDocumentType())
                .language(profile.getLanguage())
                .expectedEngine(profile.getExpectedOcrEngine())
                .qualityScore(0.90) // Placeholder
                .validationScore(validation.getExpectedConfidence())
                .retryCount(0)
                .manualReviewRequired(profile.getRequiresManualReview())
                .processingTimeMs(processingTimeMs)
                .build();
    }
}
