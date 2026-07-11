package com.courtai.exception;

import com.courtai.common.constants.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Thrown when a token has expired. Maps to HTTP 401. */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenExpiredException extends RuntimeException {
    private final ErrorCode errorCode;
    public TokenExpiredException(String message) {
        super(message);
        this.errorCode = ErrorCode.AUTH_004;
    }
    public ErrorCode getErrorCode() { return errorCode; }
}
