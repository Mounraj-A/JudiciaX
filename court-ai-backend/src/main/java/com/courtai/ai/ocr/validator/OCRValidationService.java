package com.courtai.ai.ocr.validator;

import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.dto.OCRValidationResponse;

public interface OCRValidationService {
    
    /**
     * Simulates the validation of OCR results to ensure page completeness, language compliance, and confidence limits.
     */
    OCRValidationResponse validateOCRResult(OCRProfileResponse profile);
}
