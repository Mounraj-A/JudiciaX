package com.courtai.ai.ocr.validator.impl;

import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.dto.OCRValidationResponse;
import com.courtai.ai.ocr.model.OCRDocumentType;
import com.courtai.ai.ocr.validator.OCRValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class OCRValidationServiceImpl implements OCRValidationService {

    @Override
    public OCRValidationResponse validateOCRResult(OCRProfileResponse profile) {
        log.info("Validating simulated OCR result for document UUID: {}", profile.getDocumentUuid());

        // In Phase 4, we don't have real OCR text, so we simulate validation against the profile
        boolean isDigital = profile.getDocumentType() == OCRDocumentType.DIGITAL_PDF;
        boolean isHandwritten = profile.getDocumentType() == OCRDocumentType.HANDWRITTEN_IMAGE;
        
        Double expectedConfidence = isDigital ? 0.99 : (isHandwritten ? 0.85 : 0.95);
        boolean isValid = isDigital || (!isHandwritten); 

        return OCRValidationResponse.builder()
                .isValid(isValid)
                .expectedPages(1) // Placeholder
                .expectedLanguage(profile.getLanguage())
                .expectedConfidence(expectedConfidence)
                .missingPages(new ArrayList<>())
                .unreadablePages(new ArrayList<>())
                .pageOrderCorrect(true)
                .documentComplete(true)
                .message(isValid ? "OCR Validation Passed" : "OCR Confidence below threshold, flagged for review")
                .build();
    }
}
