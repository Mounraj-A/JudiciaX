package com.courtai.ai.ocr.dto;

import com.courtai.ai.ocr.model.OCRQueueState;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OCRQueueResponse {
    private String queueId;
    private String documentUuid;
    private OCRQueueState state;
    private String message;
    private Long timestamp;
}
