package com.courtai.common.enums;

/**
 * Status of a court hearing session.
 */
public enum HearingStatus {

    /** Hearing is scheduled but has not started. */
    SCHEDULED,

    /** Hearing is currently in progress. */
    IN_PROGRESS,

    /** Hearing concluded successfully. */
    COMPLETED,

    /** Hearing was postponed — a new date will be set. */
    ADJOURNED,

    /** Hearing was cancelled and will not be rescheduled under this entry. */
    CANCELLED
}
