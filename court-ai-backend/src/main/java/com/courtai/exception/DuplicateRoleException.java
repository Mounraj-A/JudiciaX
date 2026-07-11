package com.courtai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when attempting to create a role that already exists.
 *
 * <p>Maps to HTTP 409 Conflict.</p>
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateRoleException extends RuntimeException {

    public DuplicateRoleException(String roleCode) {
        super("Role already exists: " + roleCode);
    }
}
