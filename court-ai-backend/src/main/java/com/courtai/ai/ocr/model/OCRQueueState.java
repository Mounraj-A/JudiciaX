package com.courtai.ai.ocr.model;

public enum OCRQueueState {
    REQUESTED,
    QUEUED,
    PROCESSING,
    WAITING,
    COMPLETED,
    VALIDATED,
    ARCHIVED,
    REJECTED
}
