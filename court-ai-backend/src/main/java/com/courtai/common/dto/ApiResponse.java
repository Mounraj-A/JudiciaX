package com.courtai.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard API response wrapper returned by every endpoint in the system.
 *
 * <p>Every API response — success or error — follows this uniform contract:</p>
 * <pre>
 * {
 *   "timestamp": "2024-01-15T10:30:00",
 *   "status":    200,
 *   "message":   "Operation successful",
 *   "data":      { ... },
 *   "errors":    null
 * }
 * </pre>
 *
 * @param <T> type of the {@code data} payload
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * UTC timestamp when the response was generated.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    /**
     * HTTP status code mirrored inside the body for client convenience.
     */
    private final int status;

    /**
     * Human-readable message describing the outcome.
     */
    private final String message;

    /**
     * The actual response payload. Null for error responses.
     */
    private final T data;

    /**
     * List of validation or domain error messages. Null for successful responses.
     */
    private final List<String> errors;

    // =========================================================
    //  FACTORY METHODS
    // =========================================================

    /**
     * Creates a 200 OK success response with data.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Creates a 201 Created success response with data.
     */
    public static <T> ApiResponse<T> created(String message, T data) {
        return ApiResponse.<T>builder()
                .status(201)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Creates a 200 OK success response with no data payload (e.g., delete operations).
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(message)
                .build();
    }

    /**
     * Creates an error response with a list of error details.
     */
    public static <T> ApiResponse<T> error(int status, String message, List<String> errors) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .errors(errors)
                .build();
    }

    /**
     * Creates an error response with a single error message.
     */
    public static <T> ApiResponse<T> error(int status, String message) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .errors(List.of(message))
                .build();
    }
}
