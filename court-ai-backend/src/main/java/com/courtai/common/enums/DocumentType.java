package com.courtai.common.enums;

/**
 * Enumeration of document types in the judicial case management system.
 *
 * <p>Existing values preserved for backward compatibility.
 * New values added to support expanded document classification.</p>
 */
public enum DocumentType {

    // ── Existing values (preserved) ────────────────────────────────────────
    PETITION,
    AFFIDAVIT,
    EVIDENCE,
    COURT_ORDER,
    JUDGEMENT,
    NOTICE,
    VAKALATNAMA,
    REPLY,
    WRITTEN_STATEMENT,
    CHARGE_SHEET,
    FIR,
    MEDICAL_REPORT,
    FINANCIAL_STATEMENT,
    OTHER,

    // ── New values (Phase 2 additions) ─────────────────────────────────────
    /** Medical certificate issued by a doctor or hospital. */
    MEDICAL_CERTIFICATE,
    /** Government-issued identity proof (Aadhaar, Passport, etc.). */
    IDENTITY_PROOF,
    /** Photographic evidence. */
    PHOTO,
    /** Video evidence or court recording. */
    VIDEO,
    /** Audio recording or statement. */
    AUDIO,
    /** General court order (alias for COURT_ORDER; use for new submissions). */
    ORDER
}
