package com.courtai.exception;

import com.courtai.common.constants.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Thrown when a session has expired or been revoked. Maps to HTTP 401. */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class SessionExpiredException extends RuntimeException {
    private final ErrorCode errorCode;
    public SessionExpiredException(String message) {
        super(message);
        this.errorCode = ErrorCode.AUTH_012;
    }
    public ErrorCode getErrorCode() { return errorCode; }
}
