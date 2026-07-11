package com.courtai.exception;

import com.courtai.common.constants.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Thrown when registration is attempted with an already-registered phone number. Maps to HTTP 409. */
@ResponseStatus(HttpStatus.CONFLICT)
public class PhoneAlreadyExistsException extends RuntimeException {
    private final ErrorCode errorCode;
    public PhoneAlreadyExistsException(String phone) {
        super("Phone number already registered: " + phone);
        this.errorCode = ErrorCode.AUTH_007;
    }
    public ErrorCode getErrorCode() { return errorCode; }
}
