package com.courtai.common.enums;

/**
 * Enumeration of all possible judicial case lifecycle statuses.
 *
 * <p>Values are ordered to reflect the typical progression of a case.
 * Existing values (FILED → APPEALED) are preserved for backward compatibility.
 * New values extend the workflow to support draft-to-disposal tracking.</p>
 */
public enum CaseStatus {

    // ── New workflow statuses (Phase 2 additions) ──────────────────────────
    /** Case created but not yet submitted by the advocate. */
    DRAFT,
    /** Submitted for court scrutiny. */
    SUBMITTED,
    /** Under initial scrutiny by the clerk. */
    UNDER_SCRUTINY,
    /** Registered and given a CNR number. */
    REGISTERED,
    /** Under judicial review before judge assignment. */
    UNDER_REVIEW,
    /** AI analysis completed and score attached. */
    AI_ANALYZED,
    /** Judge has been assigned to the case. */
    JUDGE_ASSIGNED,
    /** Hearing has been scheduled on the calendar. */
    HEARING_SCHEDULED,
    /** Case actively in proceedings. */
    IN_PROGRESS,
    /** Case rejected at scrutiny stage. */
    REJECTED,
    /** Case returned to advocate for corrections by clerk. */
    RETURNED,

    // ── Existing values (preserved for backward compatibility) ─────────────
    /** Case filed in court (legacy status). */
    FILED,
    /** Case admitted after initial review. */
    ADMITTED,
    /** Waiting for a hearing date. */
    PENDING_HEARING,
    /** Currently in a hearing session. */
    IN_HEARING,
    /** Hearing postponed to a future date. */
    ADJOURNED,
    /** Judgement reserved after final arguments. */
    JUDGEMENT_RESERVED,
    /** Case disposed of. */
    DISPOSED,
    /** Case formally closed. */
    CLOSED,
    /** Case taken to a higher court. */
    APPEALED
}
