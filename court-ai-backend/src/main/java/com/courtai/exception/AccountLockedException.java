package com.courtai.exception;

import com.courtai.common.constants.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Thrown when a locked account attempts to log in. Maps to HTTP 401. */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AccountLockedException extends RuntimeException {
    private final ErrorCode errorCode;
    public AccountLockedException(String message) {
        super(message);
        this.errorCode = ErrorCode.AUTH_002;
    }
    public ErrorCode getErrorCode() { return errorCode; }
}
