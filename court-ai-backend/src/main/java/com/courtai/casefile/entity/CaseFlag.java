package com.courtai.casefile.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Boolean feature flags associated with a case.
 *
 * <p>Each flag represents a sensitive or urgent dimension of the case.
 * In Phase 2, these flags become feature vectors for AI-driven priority
 * classification and urgency scoring.</p>
 *
 * <p>One-to-one with {@link CaseFile} — created when the case is registered.
 * Defaults to all {@code false}; updated by clerks/advocates during filing.</p>
 */
@Entity
@Table(
        name = "case_flags",
        indexes = {
                @Index(name = "idx_flag_case_id",    columnList = "case_id"),
                @Index(name = "idx_flag_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseFlag extends BaseEntity {

    /** The case this flag record belongs to. */
    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false, unique = true)
    private CaseFile caseFile;

    /** Case involves a medical emergency requiring urgent attention. */
    @Column(name = "medical_emergency", nullable = false)
    @Builder.Default
    private Boolean medicalEmergency = Boolean.FALSE;

    /** Case involves a child (below 18 years). */
    @Column(name = "child_involved", nullable = false)
    @Builder.Default
    private Boolean childInvolved = Boolean.FALSE;

    /** Case relates to women's safety or violence against women. */
    @Column(name = "women_safety", nullable = false)
    @Builder.Default
    private Boolean womenSafety = Boolean.FALSE;

    /** A senior citizen (above 60) is a party in this case. */
    @Column(name = "senior_citizen", nullable = false)
    @Builder.Default
    private Boolean seniorCitizen = Boolean.FALSE;

    /** A person with disability is a party in this case. */
    @Column(name = "disability", nullable = false)
    @Builder.Default
    private Boolean disability = Boolean.FALSE;

    /** Case involves financial fraud or economic offences. */
    @Column(name = "financial_fraud", nullable = false)
    @Builder.Default
    private Boolean financialFraud = Boolean.FALSE;

    /** Case involves cyber crime or digital offences. */
    @Column(name = "cyber_crime", nullable = false)
    @Builder.Default
    private Boolean cyberCrime = Boolean.FALSE;

    /** Case involves a credible threat to life. */
    @Column(name = "threat_to_life", nullable = false)
    @Builder.Default
    private Boolean threatToLife = Boolean.FALSE;

    /** Case is of high public interest or media scrutiny. */
    @Column(name = "high_public_interest", nullable = false)
    @Builder.Default
    private Boolean highPublicInterest = Boolean.FALSE;
}
