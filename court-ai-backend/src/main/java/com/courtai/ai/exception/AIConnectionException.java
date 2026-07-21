package com.courtai.ai.exception;

public class AIConnectionException extends RuntimeException {
    public AIConnectionException(String message) { super(message); }
    public AIConnectionException(String message, Throwable cause) { super(message, cause); }
}
