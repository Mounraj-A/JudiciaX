package com.courtai.exception;

import com.courtai.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Centralized global exception handler for all REST controllers.
 *
 * <p>Intercepts exceptions thrown from controllers, services, and security filters,
 * then maps them to consistent {@link ApiResponse} payloads with appropriate HTTP status codes.</p>
 *
 * <p>Handles:</p>
 * <ul>
 *   <li>Bean Validation errors (400)</li>
 *   <li>Authentication failures (401)</li>
 *   <li>Access Denied / Forbidden (403)</li>
 *   <li>Resource Not Found (404)</li>
 *   <li>Duplicate Resource (409)</li>
 *   <li>Business Rule Violations (422)</li>
 *   <li>Internal Server Error fallback (500)</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================================================
    //  400 — VALIDATION ERRORS
    // =========================================================

    /**
     * Handles {@code @Valid} / {@code @Validated} bean validation failures.
     * Collects all field-level error messages and returns them as a list.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .sorted()
                .collect(Collectors.toList());

        log.warn("Validation failed for request [{}] {}: {}",
                request.getMethod(), request.getRequestURI(), errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, "Validation failed", errors));
    }

    /**
     * Handles malformed JSON request bodies.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMalformedJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.warn("Malformed JSON request on [{}] {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, "Malformed JSON request body"));
    }

    /**
     * Handles missing required request parameters.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParams(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {

        log.warn("Missing request parameter on [{}] {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, "Missing required parameter: " + ex.getParameterName()));
    }

    /**
     * Handles type mismatch for method arguments (e.g. invalid UUID format).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String message = String.format("Parameter '%s' should be of type '%s'",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        log.warn("Type mismatch on [{}] {}: {}", request.getMethod(), request.getRequestURI(), message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, message));
    }

    // =========================================================
    //  401 — AUTHENTICATION ERRORS
    // =========================================================

    /**
     * Handles authentication failures such as invalid credentials or expired tokens.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {

        log.warn("Authentication failed on [{}] {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        String message;
        if (ex instanceof BadCredentialsException) {
            message = "Invalid username or password";
        } else if (ex instanceof DisabledException) {
            message = "Account is disabled";
        } else if (ex instanceof LockedException) {
            message = "Account is locked";
        } else {
            message = "Authentication failed";
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(401, message));
    }

    // =========================================================
    //  403 — ACCESS DENIED
    // =========================================================

    /**
     * Handles insufficient authorization — user authenticated but lacks required role/permission.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn("Access denied on [{}] {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(403, "Access denied: You do not have permission to perform this action"));
    }

    // =========================================================
    //  404 — RESOURCE NOT FOUND
    // =========================================================

    /**
     * Handles requests for non-existent resources.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Resource not found on [{}] {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, ex.getMessage()));
    }

    /**
     * Handles unsupported HTTP method requests.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        log.warn("Method not supported on [{}] {}", request.getMethod(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(405, "HTTP method not supported: " + ex.getMethod()));
    }

    // =========================================================
    //  409 — DUPLICATE RESOURCE
    // =========================================================

    /**
     * Handles duplicate resource creation conflicts.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResourceException(
            DuplicateResourceException ex,
            HttpServletRequest request) {

        log.warn("Duplicate resource on [{}] {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(409, ex.getMessage()));
    }

    // =========================================================
    //  422 — BUSINESS RULE VIOLATION
    // =========================================================

    /**
     * Handles business logic violations.
     */
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessRuleViolation(
            BusinessRuleViolationException ex,
            HttpServletRequest request) {

        log.warn("Business rule violation on [{}] {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error(422, ex.getMessage()));
    }

    // =========================================================
    //  500 — INTERNAL SERVER ERROR (Catch-All)
    // =========================================================

    /**
     * Catch-all handler for any unhandled exceptions.
     * Logs the full stack trace but returns a safe generic message to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error on [{}] {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "An unexpected internal server error occurred. Please try again later."));
    }
}
