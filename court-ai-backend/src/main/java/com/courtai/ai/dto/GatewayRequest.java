package com.courtai.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The standard request wrapper for the AI Gateway.
 * Carries the context and the raw payload intended for the specific AI engine.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayRequest<T> {
    private AIRequestContext context;
    private String targetService; // e.g., "ocr", "priority"
    private T payload;
}
