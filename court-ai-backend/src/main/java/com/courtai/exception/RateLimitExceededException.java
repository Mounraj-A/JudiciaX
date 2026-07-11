package com.courtai.exception;

import com.courtai.common.constants.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Thrown when a client exceeds the configured rate limit. Maps to HTTP 429. */
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class RateLimitExceededException extends RuntimeException {
    private final ErrorCode errorCode;
    public RateLimitExceededException(String message) {
        super(message);
        this.errorCode = ErrorCode.AUTH_013;
    }
    public ErrorCode getErrorCode() { return errorCode; }
}
