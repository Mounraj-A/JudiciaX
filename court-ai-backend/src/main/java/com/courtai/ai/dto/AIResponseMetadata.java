package com.courtai.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standardized metadata returned in every AI response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIResponseMetadata {
    private String correlationId;
    private String requestId;
    private String gatewayVersion;
    private String aiModelVersion;
    private String apiVersion;
    private long processingTimeMs;
    private long inferenceTimeMs;
    private double confidence;
    private LocalDateTime generatedTimestamp;
    private String status;
}
