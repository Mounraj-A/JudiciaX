package com.courtai.ai.workflow.state;

public enum WorkflowState {
    CREATED,
    VALIDATED,
    QUEUED,
    RUNNING,
    WAITING,
    PARTIALLY_COMPLETED,
    COMPLETED,
    FAILED,
    CANCELLED,
    RETRYING,
    ARCHIVED
}
