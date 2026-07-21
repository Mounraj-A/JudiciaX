package com.courtai.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The standard response wrapper returned by the AI Gateway.
 * Carries the result payload and the standardized metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayResponse<T> {
    private AIResponseMetadata metadata;
    private T payload;
}
