package com.courtai.casefile.entity;

import com.courtai.casecategory.entity.CaseCategory;
import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.CasePriority;
import com.courtai.common.enums.CaseStatus;
import com.courtai.common.enums.CaseType;
import com.courtai.court.entity.Court;
import com.courtai.judge.entity.Judge;
import com.courtai.advocate.entity.Advocate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

/**
 * Core Case File entity — the Aggregate Root of the judicial domain.
 *
 * <p>All judicial domain objects ({@link CaseParty}, {@link CaseAssignment},
 * {@link CaseStatusHistory}, {@link CaseFlag}, hearings, documents, evidence,
 * judge notes, AI analysis) reference this entity as their root.</p>
 *
 * <p>AI prioritization scores and scheduling logic will be populated in Phase 2.</p>
 */
@Entity
@Table(
        name = "case_files",
        indexes = {
                @Index(name = "idx_case_number",     columnList = "case_number"),
                @Index(name = "idx_case_status",     columnList = "status"),
                @Index(name = "idx_case_judge_id",   columnList = "assigned_judge_id"),
                @Index(name = "idx_case_is_deleted", columnList = "is_deleted"),
                @Index(name = "idx_case_court_id",   columnList = "court_id"),
                @Index(name = "idx_case_priority",   columnList = "priority"),
                @Index(name = "idx_case_cnr",        columnList = "cnr_number")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseFile extends BaseEntity {

    // ── Core Identifiers ──────────────────────────────────────────────────

    @NotBlank
    @Column(name = "case_number", nullable = false, unique = true, length = 50)
    private String caseNumber;

    /**
     * Court Number Record — unique national identifier assigned at registration.
     * Format: STCRTYPDDDDYYYY (e.g., MHHC010012342024)
     */
    @Column(name = "cnr_number", unique = true, length = 50)
    private String cnrNumber;

    @Column(name = "filing_year")
    private Integer filingYear;

    // ── Case Description ──────────────────────────────────────────────────

    @NotBlank
    @Column(name = "case_title", nullable = false, length = 500)
    private String caseTitle;

    @Column(name = "case_description", columnDefinition = "TEXT")
    private String caseDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "case_type", nullable = false, length = 50)
    private CaseType caseType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private CaseStatus status = CaseStatus.FILED;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    @Builder.Default
    private CasePriority priority = CasePriority.LOW;

    // ── Dates ─────────────────────────────────────────────────────────────

    @Column(name = "filing_date")
    private LocalDate filingDate;

    @Column(name = "hearing_date")
    private LocalDate hearingDate;

    // ── AI Score ──────────────────────────────────────────────────────────

    /** AI-computed priority score (0–100). Higher = more urgent. Populated in Phase 2. */
    @Column(name = "priority_score")
    private Double priorityScore;

    // ── Relationships: Court & Category ──────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id")
    private Court court;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_category_id")
    private CaseCategory caseCategory;

    // ── Relationships: People ─────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_judge_id")
    private Judge assignedJudge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petitioner_advocate_id")
    private Advocate petitionerAdvocate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "respondent_advocate_id")
    private Advocate respondentAdvocate;

    // ── Party Names (denormalised) ────────────────────────────────────────

    @Column(name = "petitioner_name", length = 200)
    private String petitionerName;

    @Column(name = "respondent_name", length = 200)
    private String respondentName;

    @Column(name = "court_name", length = 200)
    private String courtName;

    // ── Legal Details ─────────────────────────────────────────────────────

    /** Police station where FIR was filed (for criminal cases). */
    @Column(name = "police_station", length = 200)
    private String policeStation;

    /** Applicable acts and sections — e.g., "IPC 302, 307; CrPC 154". */
    @Column(name = "act_section", length = 500)
    private String actSection;

    // ── Clerk Scrutiny Fields (added V17) ────────────────────────────────

    /**
     * Official case registration number generated by the clerk.
     * Format: STATE-DISTRICT-COURT_CODE-YEAR-SEQUENCE, e.g. TN-COIMBATORE-DC-2026-000001.
     * Populated only after the case status reaches REGISTERED.
     */
    @Column(name = "official_case_number", unique = true, length = 80)
    private String officialCaseNumber;

    /** Timestamp when the clerk formally registered this case. */
    @Column(name = "registered_at")
    private java.time.LocalDateTime registeredAt;

    /** UUID of the clerk who registered the case. */
    @Column(name = "registered_by_uuid", length = 36)
    private String registeredByUuid;

    /** UUID of the clerk currently handling scrutiny. */
    @Column(name = "scrutiny_clerk_uuid", length = 36)
    private String scrutinyClerkUuid;

    /** Clerk's verification remarks on the overall case. */
    @Column(name = "verification_remarks", columnDefinition = "TEXT")
    private String verificationRemarks;

    /**
     * Comma-separated UUIDs of potential duplicate cases.
     * Populated by {@code DuplicateDetectionService}.
     */
    @Column(name = "duplicate_case_uuids", columnDefinition = "TEXT")
    private String duplicateCaseUuids;

    /** Whether a duplicate check has been performed for this case. */
    @Column(name = "is_duplicate_checked", nullable = false)
    @Builder.Default
    private Boolean isDuplicateChecked = Boolean.FALSE;

    /** Whether jurisdiction has been verified for this case. */
    @Column(name = "is_jurisdiction_verified", nullable = false)
    @Builder.Default
    private Boolean isJurisdictionVerified = Boolean.FALSE;

    /** Position in the pending judge assignment queue. Lower = higher priority. */
    @Column(name = "judge_queue_position")
    private Integer judgeQueuePosition;

    /** Timestamp when the case was placed in the judge assignment queue. */
    @Column(name = "judge_queued_at")
    private java.time.LocalDateTime judgeQueuedAt;
}
