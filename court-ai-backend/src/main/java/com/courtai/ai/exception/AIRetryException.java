package com.courtai.ai.exception;

public class AIRetryException extends RuntimeException {
    public AIRetryException(String message) { super(message); }
    public AIRetryException(String message, Throwable cause) { super(message, cause); }
}
