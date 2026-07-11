package com.courtai.common.enums;

/**
 * Classification of evidence submitted to the court.
 */
public enum EvidenceType {

    /** Written documents, records, or certificates. */
    DOCUMENTARY,

    /** Physical objects presented as evidence. */
    PHYSICAL,

    /** Digital data — emails, CCTV footage, device extracts. */
    DIGITAL,

    /** Verbal or written statement from a witness. */
    WITNESS_STATEMENT,

    /** Opinion from a qualified expert (forensic, medical, financial). */
    EXPERT_OPINION,

    /** Any evidence not covered by the above categories. */
    OTHER
}
