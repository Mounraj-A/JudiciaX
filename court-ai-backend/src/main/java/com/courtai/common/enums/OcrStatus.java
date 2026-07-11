package com.courtai.common.enums;

/**
 * OCR (Optical Character Recognition) processing status for uploaded documents.
 *
 * <p>Used by the AI pipeline to track text extraction progress.</p>
 */
public enum OcrStatus {

    /** OCR not yet triggered. */
    PENDING,

    /** OCR currently being processed. */
    IN_PROGRESS,

    /** OCR completed successfully — text is available. */
    COMPLETED,

    /** OCR failed — manual review required. */
    FAILED,

    /** Document type does not require OCR (e.g., video, audio). */
    NOT_APPLICABLE
}
