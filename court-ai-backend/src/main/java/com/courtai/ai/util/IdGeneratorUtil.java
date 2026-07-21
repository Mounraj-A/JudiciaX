package com.courtai.ai.util;

import java.util.UUID;

/**
 * Utility for generating standardized IDs for the AI Gateway.
 */
public final class IdGeneratorUtil {

    private IdGeneratorUtil() {
        // Utility class
    }

    /**
     * Generates a unique Request ID for an AI Gateway call.
     * @return A string representing a UUID based request ID.
     */
    public static String generateRequestId() {
        return "REQ-AI-" + UUID.randomUUID().toString().toUpperCase();
    }

    /**
     * Generates a unique Correlation ID for tracing a flow through multiple services.
     * @return A string representing a UUID based correlation ID.
     */
    public static String generateCorrelationId() {
        return "CORR-" + UUID.randomUUID().toString().toUpperCase();
    }
}
