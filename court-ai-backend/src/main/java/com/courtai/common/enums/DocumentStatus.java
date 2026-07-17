package com.courtai.common.enums;

/**
 * Full lifecycle status of a judicial document.
 *
 * <p>Progresses through upload → validation → storage → OCR pipeline → verified → archived.</p>
 */
public enum DocumentStatus {

    /** Document has been uploaded but not yet validated. */
    UPLOADED,

    /** Document is awaiting clerk verification. */
    PENDING_VERIFICATION,

    /** Clerk has verified and accepted the document. */
    VERIFIED,

    /** Clerk has rejected the document — advocate must replace. */
    REJECTED,

    /** Clerk has requested a replacement from the advocate. */
    REPLACEMENT_REQUESTED,

    /** Document has been archived (read-only, permanent retention). */
    ARCHIVED
}
