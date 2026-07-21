package com.courtai.ai.ocr.profile;

import com.courtai.ai.ocr.dto.OCRProfileRequest;
import com.courtai.ai.ocr.dto.OCRProfileResponse;

public interface OCRProfileService {
    
    /**
     * Generates a comprehensive OCR profile for a document outlining its expected complexity and requirements.
     */
    OCRProfileResponse generateProfile(OCRProfileRequest request);
}
