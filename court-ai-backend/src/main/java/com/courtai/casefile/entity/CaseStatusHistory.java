package com.courtai.casefile.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.CaseStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Immutable audit record of every case status transition.
 *
 * <p>Every time a case moves from one {@link CaseStatus} to another,
 * a new {@code CaseStatusHistory} record is created. Records are never
 * updated or deleted — they form an append-only audit trail.</p>
 */
@Entity
@Table(
        name = "case_status_history",
        indexes = {
                @Index(name = "idx_csh_case_id",    columnList = "case_id"),
                @Index(name = "idx_csh_changed_at", columnList = "changed_at"),
                @Index(name = "idx_csh_to_status",  columnList = "to_status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseStatusHistory extends BaseEntity {

    /** The case whose status changed. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    /** Previous status before the transition. Null for the first status entry. */
    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 30)
    private CaseStatus fromStatus;

    /** New status after the transition. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 30)
    private CaseStatus toStatus;

    /** Timestamp when the status transition occurred. */
    @NotNull
    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    /** UUID of the user who triggered the transition. */
    @Column(name = "changed_by_uuid", length = 36)
    private String changedByUuid;

    /** Role of the actor who made the change. */
    @Column(name = "changed_by_role", length = 30)
    private String changedByRole;

    /** Optional remarks explaining the reason for the status change. */
    @Column(name = "remarks", length = 500)
    private String remarks;
}
