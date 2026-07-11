package com.courtai.common.enums;

/**
 * Processing status of a case in the AI analysis queue.
 *
 * <p>Used by {@link com.courtai.ai.entity.CaseAIQueue} to coordinate
 * asynchronous case analysis with the FastAPI AI service.</p>
 */
public enum QueueStatus {

    /** Waiting to be picked up by the AI processor. */
    PENDING,

    /** Currently being processed by the AI service. */
    PROCESSING,

    /** Analysis completed and results stored in CaseAnalysis. */
    COMPLETED,

    /** Processing failed after max retry attempts. */
    FAILED,

    /** Case skipped intentionally (e.g., manually prioritized). */
    SKIPPED
}
