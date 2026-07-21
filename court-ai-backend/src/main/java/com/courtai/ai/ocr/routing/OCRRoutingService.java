package com.courtai.ai.ocr.routing;

import com.courtai.ai.ocr.dto.OCRDecisionResponse;
import com.courtai.ai.ocr.dto.OCRProfileResponse;

public interface OCRRoutingService {
    
    /**
     * Maps the OCR decision and profile into a concrete routing action for the Workflow Engine.
     * Returns the route string (e.g. OCR_PIPELINE, MANUAL_REVIEW, REJECTED, DEFERRED, FUTURE_SPEECH).
     */
    String determineRoute(OCRProfileResponse profile, OCRDecisionResponse decision);
}
