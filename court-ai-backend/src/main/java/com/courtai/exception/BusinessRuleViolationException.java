package com.courtai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a business rule is violated.
 *
 * <p>Maps to HTTP 422 Unprocessable Entity.</p>
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class BusinessRuleViolationException extends RuntimeException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }

    public BusinessRuleViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
