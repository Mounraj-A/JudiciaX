package com.courtai.exception;

import com.courtai.common.constants.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Thrown when registration is attempted with an already-registered email. Maps to HTTP 409. */
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends RuntimeException {
    private final ErrorCode errorCode;
    public EmailAlreadyExistsException(String email) {
        super("Email already registered: " + email);
        this.errorCode = ErrorCode.AUTH_006;
    }
    public ErrorCode getErrorCode() { return errorCode; }
}
