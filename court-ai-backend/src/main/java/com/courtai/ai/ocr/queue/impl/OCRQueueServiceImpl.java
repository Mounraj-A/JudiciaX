package com.courtai.ai.ocr.queue.impl;

import com.courtai.ai.ocr.dto.OCRQueueResponse;
import com.courtai.ai.ocr.model.OCRQueueState;
import com.courtai.ai.ocr.queue.OCRQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class OCRQueueServiceImpl implements OCRQueueService {

    // Simulating an in-memory queue state for Phase 4 orchestration logic
    private final Map<String, OCRQueueResponse> queueStore = new ConcurrentHashMap<>();

    @Override
    public OCRQueueResponse updateQueueState(String documentUuid, OCRQueueState newState, String message) {
        log.info("Transitioning queue state for document {} to {}", documentUuid, newState);
        
        OCRQueueResponse response = OCRQueueResponse.builder()
                .queueId(UUID.randomUUID().toString())
                .documentUuid(documentUuid)
                .state(newState)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
                
        queueStore.put(documentUuid, response);
        return response;
    }

    @Override
    public OCRQueueResponse getQueueState(String documentUuid) {
        return queueStore.getOrDefault(documentUuid, 
                OCRQueueResponse.builder()
                        .documentUuid(documentUuid)
                        .state(OCRQueueState.REQUESTED)
                        .message("Not found in active queue.")
                        .build());
    }
}
