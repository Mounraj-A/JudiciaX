package com.courtai.clerk.entity;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.ObjectionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a formal objection raised by a clerk during case scrutiny.
 *
 * <p>When a clerk finds an issue with a submitted case — missing documents,
 * jurisdiction mismatch, duplicate case, or incomplete information — they
 * raise an objection which is stored here. The case is then returned to
 * the advocate who must resolve the objection before re-submission.</p>
 *
 * <p>Records are never deleted — {@code isResolved} tracks the lifecycle.</p>
 */
@Entity
@Table(
        name = "case_objections",
        indexes = {
                @Index(name = "idx_objection_case_id",     columnList = "case_id"),
                @Index(name = "idx_objection_clerk_uuid",  columnList = "raised_by_clerk_uuid"),
                @Index(name = "idx_objection_is_resolved", columnList = "is_resolved")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseObjection extends BaseEntity {

    /** The case this objection is raised against. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    /** UUID of the clerk who raised this objection. */
    @NotBlank
    @Column(name = "raised_by_clerk_uuid", nullable = false, length = 36)
    private String raisedByClerkUuid;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "objection_type", nullable = false, length = 50)
    private ObjectionType objectionType;

    /** Human-readable reason for the objection. */
    @NotBlank
    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    /**
     * Comma-separated list or free-text description of missing documents.
     * Only populated when {@code objectionType = MISSING_DOCUMENT}.
     */
    @Column(name = "missing_documents", columnDefinition = "TEXT")
    private String missingDocuments;

    /** Description of what corrections must be made by the advocate. */
    @Column(name = "correction_required", columnDefinition = "TEXT")
    private String correctionRequired;

    /** Whether the advocate has resolved this objection. */
    @Column(name = "is_resolved", nullable = false)
    @Builder.Default
    private Boolean isResolved = Boolean.FALSE;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    /** UUID of the user who marked this objection resolved. */
    @Column(name = "resolved_by_uuid", length = 36)
    private String resolvedByUuid;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;
}
