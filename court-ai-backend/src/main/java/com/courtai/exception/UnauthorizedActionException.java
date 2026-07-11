package com.courtai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a user attempts an action they are not authorised to perform,
 * beyond simple permission checks (e.g., ownership violation, system-role mutation attempt).
 *
 * <p>Maps to HTTP 403 Forbidden.</p>
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedActionException extends RuntimeException {

    public UnauthorizedActionException(String message) {
        super(message);
    }
}
