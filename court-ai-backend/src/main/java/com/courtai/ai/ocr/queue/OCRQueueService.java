package com.courtai.ai.ocr.queue;

import com.courtai.ai.ocr.dto.OCRQueueResponse;
import com.courtai.ai.ocr.model.OCRQueueState;

public interface OCRQueueService {
    
    /**
     * Updates and retrieves the current queue status for a document.
     */
    OCRQueueResponse updateQueueState(String documentUuid, OCRQueueState newState, String message);
    
    /**
     * Retrieves the current queue state without modifying it.
     */
    OCRQueueResponse getQueueState(String documentUuid);
}
