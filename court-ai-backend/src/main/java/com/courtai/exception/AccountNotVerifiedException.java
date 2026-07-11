package com.courtai.exception;

import com.courtai.common.constants.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Thrown when an unverified or unapproved account tries to log in. Maps to HTTP 403. */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccountNotVerifiedException extends RuntimeException {
    private final ErrorCode errorCode;
    public AccountNotVerifiedException(String message) {
        super(message);
        this.errorCode = ErrorCode.AUTH_005;
    }
    public ErrorCode getErrorCode() { return errorCode; }
}
