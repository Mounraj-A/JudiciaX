package com.courtai.common.enums;

/**
 * Classification of objections that a clerk may raise during case scrutiny.
 */
public enum ObjectionType {

    /** Required document not uploaded by advocate. */
    MISSING_DOCUMENT,

    /** Uploaded document is illegible, forged, or incorrect. */
    INVALID_DOCUMENT,

    /** Case filed in the wrong court (jurisdiction mismatch). */
    JURISDICTION_MISMATCH,

    /** A substantially similar case already exists in the system. */
    DUPLICATE_CASE,

    /** Mandatory case fields are incomplete or contradictory. */
    INCOMPLETE_INFORMATION,

    /** Any other objection not covered above. */
    OTHER
}
