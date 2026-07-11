package com.courtai.exception;

import com.courtai.common.constants.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Thrown when an incorrect OTP is submitted. Maps to HTTP 400. */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidOtpException extends RuntimeException {
    private final ErrorCode errorCode;
    public InvalidOtpException(String message) {
        super(message);
        this.errorCode = ErrorCode.AUTH_008;
    }
    public ErrorCode getErrorCode() { return errorCode; }
}
