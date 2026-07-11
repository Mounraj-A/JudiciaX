package com.courtai.exception;

import com.courtai.common.constants.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Thrown when a submitted token is invalid (bad format, not found, or already used). Maps to HTTP 400. */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTokenException extends RuntimeException {
    private final ErrorCode errorCode;
    public InvalidTokenException(String message) {
        super(message);
        this.errorCode = ErrorCode.VAL_004;
    }
    public ErrorCode getErrorCode() { return errorCode; }
}
