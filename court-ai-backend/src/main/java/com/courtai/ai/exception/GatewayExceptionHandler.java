package com.courtai.ai.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Global exception handler for the AI Gateway.
 * Catches AI-specific exceptions and formats them as standard HTTP responses.
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.courtai.ai.controller")
public class GatewayExceptionHandler {

    @ExceptionHandler(AIValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(AIValidationException ex) {
        log.warn("AI Validation Failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Validation Error", "message", ex.getMessage()));
    }

    @ExceptionHandler(AITimeoutException.class)
    public ResponseEntity<Map<String, String>> handleTimeoutException(AITimeoutException ex) {
        log.error("AI Gateway Timeout: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(Map.of("error", "Gateway Timeout", "message", ex.getMessage()));
    }

    @ExceptionHandler(AIUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleUnavailableException(AIUnavailableException ex) {
        log.error("AI Service Unavailable: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Service Unavailable", "message", ex.getMessage()));
    }

    @ExceptionHandler({AIConnectionException.class, AIRetryException.class, AIResponseException.class})
    public ResponseEntity<Map<String, String>> handleConnectionException(RuntimeException ex) {
        log.error("AI Gateway Communication Error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Gateway Error", "message", ex.getMessage()));
    }
}
