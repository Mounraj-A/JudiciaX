package com.courtai.exception;

import com.courtai.common.constants.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Thrown when an OTP has expired. Maps to HTTP 400. */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OtpExpiredException extends RuntimeException {
    private final ErrorCode errorCode;
    public OtpExpiredException(String message) {
        super(message);
        this.errorCode = ErrorCode.AUTH_009;
    }
    public ErrorCode getErrorCode() { return errorCode; }
}
