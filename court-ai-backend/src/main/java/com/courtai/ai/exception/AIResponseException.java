package com.courtai.ai.exception;

public class AIResponseException extends RuntimeException {
    public AIResponseException(String message) { super(message); }
    public AIResponseException(String message, Throwable cause) { super(message, cause); }
}
