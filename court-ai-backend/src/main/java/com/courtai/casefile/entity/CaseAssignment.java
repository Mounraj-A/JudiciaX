package com.courtai.casefile.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.judge.entity.Judge;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Records the assignment of a judge to a case.
 *
 * <p>A case may be reassigned over its lifecycle. Only one assignment
 * should be active ({@code isActive = true}) at any time.
 * Historical assignments are preserved for audit purposes.</p>
 */
@Entity
@Table(
        name = "case_assignments",
        indexes = {
                @Index(name = "idx_assign_case_id",    columnList = "case_id"),
                @Index(name = "idx_assign_judge_id",   columnList = "judge_id"),
                @Index(name = "idx_assign_is_active",  columnList = "is_active"),
                @Index(name = "idx_assign_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseAssignment extends BaseEntity {

    /** The case being assigned. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    /** The judge assigned to the case. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "judge_id", nullable = false)
    private Judge judge;

    /** Timestamp when the assignment was made. */
    @NotNull
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    /** Timestamp when the judge was unassigned (null if currently active). */
    @Column(name = "unassigned_at")
    private LocalDateTime unassignedAt;

    /** Reason for this assignment or reassignment. */
    @Column(name = "assignment_reason", length = 500)
    private String assignmentReason;

    /** UUID of the admin or system actor who made this assignment. */
    @Column(name = "assigned_by_uuid", length = 36)
    private String assignedByUuid;

    /** Whether this is the currently active assignment for the case. */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;
}
