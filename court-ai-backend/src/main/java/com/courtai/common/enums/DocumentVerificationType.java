package com.courtai.common.enums;

/**
 * Type of document verification performed.
 */
public enum DocumentVerificationType {
    /** Standard clerk review for authenticity and completeness. */
    CLERK_REVIEW,
    /** Judge review for evidentiary value. */
    JUDGE_REVIEW,
    /** AI-assisted automated verification. */
    AI_VERIFICATION,
    /** Manual admin override. */
    ADMIN_REVIEW
}
