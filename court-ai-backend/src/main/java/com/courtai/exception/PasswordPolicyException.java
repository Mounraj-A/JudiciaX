package com.courtai.exception;

import com.courtai.common.constants.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/** Thrown when a new password violates the password policy. Maps to HTTP 422. */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class PasswordPolicyException extends RuntimeException {
    private final ErrorCode errorCode;
    private final List<String> violations;

    public PasswordPolicyException(List<String> violations) {
        super("Password policy violation: " + String.join(", ", violations));
        this.errorCode = ErrorCode.USER_002;
        this.violations = violations;
    }

    public PasswordPolicyException(String message) {
        super(message);
        this.errorCode = ErrorCode.USER_002;
        this.violations = List.of(message);
    }

    public ErrorCode getErrorCode() { return errorCode; }
    public List<String> getViolations() { return violations; }
}
