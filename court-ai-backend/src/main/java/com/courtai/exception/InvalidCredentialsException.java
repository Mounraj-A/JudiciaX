package com.courtai.exception;

import com.courtai.common.constants.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Thrown when email/password credentials are invalid. Maps to HTTP 401. */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends RuntimeException {
    private final ErrorCode errorCode;
    public InvalidCredentialsException(String message) {
        super(message);
        this.errorCode = ErrorCode.AUTH_001;
    }
    public ErrorCode getErrorCode() { return errorCode; }
}
