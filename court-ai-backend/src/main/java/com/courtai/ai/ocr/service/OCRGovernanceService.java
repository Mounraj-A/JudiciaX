package com.courtai.ai.ocr.service;

import com.courtai.ai.ocr.dto.OCRProfileRequest;
import com.courtai.ai.ocr.dto.OCRWorkflowResponse;

public interface OCRGovernanceService {
    
    /**
     * The overarching facade that orchestrates the OCR Intelligence Layer:
     * Profile -> Strategy -> Quality -> Decision -> Validation -> Routing -> Queue -> Telemetry.
     */
    OCRWorkflowResponse processOCRWorkflow(OCRProfileRequest request);
}
