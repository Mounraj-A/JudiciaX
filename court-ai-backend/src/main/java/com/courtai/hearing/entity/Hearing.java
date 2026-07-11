package com.courtai.hearing.entity;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.HearingStatus;
import com.courtai.court.entity.CourtRoom;
import com.courtai.judge.entity.Judge;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a scheduled or completed court hearing for a case.
 *
 * <p>Multiple hearings can exist for a single case. The sequence is tracked
 * via {@code hearingNumber}. Adjournments create a new hearing entry with
 * {@code adjournReason} and a {@code nextHearingDate}.</p>
 */
@Entity
@Table(
        name = "hearings",
        indexes = {
                @Index(name = "idx_hearing_case_id",    columnList = "case_id"),
                @Index(name = "idx_hearing_status",     columnList = "status"),
                @Index(name = "idx_hearing_scheduled",  columnList = "scheduled_at"),
                @Index(name = "idx_hearing_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hearing extends BaseEntity {

    /** The case this hearing belongs to. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    /** The courtroom where this hearing is scheduled. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_room_id")
    private CourtRoom courtRoom;

    /** Judge presiding over this hearing (may differ from assigned judge). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "judge_id")
    private Judge judge;

    /** Scheduled date and time of the hearing. */
    @NotNull
    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    /** Actual time the hearing started. */
    @Column(name = "actual_start_at")
    private LocalDateTime actualStartAt;

    /** Actual time the hearing ended. */
    @Column(name = "actual_end_at")
    private LocalDateTime actualEndAt;

    /** Current status of this hearing session. */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private HearingStatus status = HearingStatus.SCHEDULED;

    /** Reason for adjournment, if applicable. */
    @Column(name = "adjourn_reason", length = 500)
    private String adjournReason;

    /** Next scheduled hearing date after an adjournment. */
    @Column(name = "next_hearing_date")
    private LocalDate nextHearingDate;

    /** Sequential hearing number within the case (1, 2, 3, ...). */
    @Column(name = "hearing_number")
    private Integer hearingNumber;

    /** Judge's notes or orders from this hearing. */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /** Whether this hearing was conducted via video conferencing. */
    @Column(name = "is_virtual", nullable = false)
    @Builder.Default
    private Boolean isVirtual = Boolean.FALSE;
}
