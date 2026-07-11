package com.courtai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested {@link com.courtai.auth.entity.Role} cannot be found.
 *
 * <p>Maps to HTTP 404 Not Found.</p>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(String identifier) {
        super("Role not found: " + identifier);
    }
}
