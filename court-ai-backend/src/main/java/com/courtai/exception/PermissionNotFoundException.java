package com.courtai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested {@link com.courtai.auth.entity.Permission} cannot be found.
 *
 * <p>Maps to HTTP 404 Not Found.</p>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PermissionNotFoundException extends RuntimeException {

    public PermissionNotFoundException(String identifier) {
        super("Permission not found: " + identifier);
    }
}
