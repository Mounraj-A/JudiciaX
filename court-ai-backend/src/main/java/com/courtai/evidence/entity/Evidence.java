package com.courtai.evidence.entity;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.EvidenceType;
import com.courtai.document.entity.Document;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * Represents a piece of evidence submitted in a judicial case.
 *
 * <p>Evidence may be linked to a {@link Document} (for documentary/digital
 * evidence) or stand alone (physical objects, verbal testimony).</p>
 *
 * <p>Admissibility and court verification are tracked via the
 * {@link EvidenceVerification} entity (one-to-one).</p>
 */
@Entity
@Table(
        name = "evidence",
        indexes = {
                @Index(name = "idx_evidence_case_id",    columnList = "case_id"),
                @Index(name = "idx_evidence_type",       columnList = "evidence_type"),
                @Index(name = "idx_evidence_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evidence extends BaseEntity {

    /** The case this evidence is submitted for. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    /** Classification of the evidence. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "evidence_type", nullable = false, length = 30)
    private EvidenceType evidenceType;

    /** Short descriptive title of the evidence item. */
    @NotBlank
    @Column(name = "title", nullable = false, length = 300)
    private String title;

    /** Detailed description of what the evidence is and its relevance. */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Optional reference to a document stored in the system.
     * Null for physical objects or verbal witness statements.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    /** Date the evidence was collected or obtained. */
    @Column(name = "collected_at")
    private LocalDate collectedAt;

    /** Name/designation of who collected or submitted the evidence. */
    @Column(name = "collected_by", length = 200)
    private String collectedBy;

    /** Location where evidence was found or collected. */
    @Column(name = "location", length = 500)
    private String location;

    /** Whether the court has admitted this evidence for consideration. */
    @Column(name = "is_admitted", nullable = false)
    @Builder.Default
    private Boolean isAdmitted = Boolean.FALSE;

    /** Judge's remarks on evidence admission or rejection. */
    @Column(name = "admission_remarks", length = 500)
    private String admissionRemarks;

    // ── Clerk Verification Fields (added V17) ─────────────────────────────

    /** Whether the clerk has verified this evidence item. */
    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = Boolean.FALSE;

    /** UUID of the clerk who verified or rejected this evidence. */
    @Column(name = "verified_by_uuid", length = 36)
    private String verifiedByUuid;

    /** Timestamp when the clerk performed the verification. */
    @Column(name = "verified_at")
    private java.time.LocalDateTime verifiedAt;

    /** Clerk's verification remarks. */
    @Column(name = "verification_remarks", length = 500)
    private String verificationRemarks;

    /** Reason provided when evidence is rejected by the clerk. */
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;
}
