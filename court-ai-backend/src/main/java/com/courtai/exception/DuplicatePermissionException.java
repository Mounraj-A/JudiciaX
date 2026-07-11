package com.courtai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when attempting to create a permission code that already exists.
 *
 * <p>Maps to HTTP 409 Conflict.</p>
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicatePermissionException extends RuntimeException {

    public DuplicatePermissionException(String permissionCode) {
        super("Permission already exists: " + permissionCode);
    }
}
