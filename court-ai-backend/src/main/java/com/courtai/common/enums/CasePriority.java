package com.courtai.common.enums;

/**
 * Case priority levels used for AI-driven scheduling and queue management.
 *
 * <p>Priority is computed by the AI engine and may be overridden by a judge.
 * CRITICAL cases receive immediate calendar allocation.</p>
 */
public enum CasePriority {

    /** Standard priority — default for new cases. */
    LOW,

    /** Elevated priority — requires scheduling within 30 days. */
    MEDIUM,

    /** High priority — requires scheduling within 7 days. */
    HIGH,

    /** Critical priority — requires immediate judicial attention. */
    CRITICAL
}
