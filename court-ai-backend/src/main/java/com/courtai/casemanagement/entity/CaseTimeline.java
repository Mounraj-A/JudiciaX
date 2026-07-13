package com.courtai.casemanagement.entity;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Append-only audit trail of every lifecycle event on a case.
 *
 * <p>Every status transition, assignment, transfer, archive, clone,
 * and actor interaction produces one {@code CaseTimeline} entry.
 * Records are immutable once created — never updated or deleted.</p>
 */
@Entity
@Table(
        name = "case_timelines",
        indexes = {
                @Index(name = "idx_ct_case_id",    columnList = "case_id"),
                @Index(name = "idx_ct_event_type", columnList = "event_type"),
                @Index(name = "idx_ct_event_time", columnList = "event_time")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseTimeline extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    /**
     * Type of lifecycle event.
     * Examples: CASE_CREATED, SUBMITTED, RETURNED, REGISTERED, JUDGE_ASSIGNED,
     *           HEARING_SCHEDULED, ADJOURNED, JUDGMENT_RESERVED, DISPOSED,
     *           CLOSED, ARCHIVED, CANCELLED, TRANSFERRED, REOPENED, CLONED
     */
    @NotBlank
    @Column(name = "event_type", nullable = false, length = 60)
    private String eventType;

    /** Human-readable label for the event. */
    @Column(name = "event_label", length = 200)
    private String eventLabel;

    /** Optional description or remarks about the event. */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** UUID of the user who triggered this event. */
    @Column(name = "actor_uuid", length = 36)
    private String actorUuid;

    /** Role of the actor (e.g., ROLE_ADVOCATE, ROLE_CLERK, ROLE_JUDGE, ROLE_ADMIN). */
    @Column(name = "actor_role", length = 30)
    private String actorRole;

    /** Display name of the actor for UI rendering. */
    @Column(name = "actor_name", length = 200)
    private String actorName;

    /** Reason provided by the actor, if applicable. */
    @Column(name = "reason", length = 500)
    private String reason;

    /** Explicit event timestamp (defaults to BaseEntity.createdAt). */
    @NotNull
    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;
}
