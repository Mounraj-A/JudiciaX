package com.courtai.casemanagement.entity;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Records every transfer event affecting a case — court, judge, clerk, or bench.
 *
 * <p>Each transfer creates one immutable {@code CaseTransfer} row.
 * Transfers also produce {@link CaseTimeline} entries and audit logs.</p>
 */
@Entity
@Table(
        name = "case_transfers",
        indexes = {
                @Index(name = "idx_transfer_case_id",     columnList = "case_id"),
                @Index(name = "idx_transfer_type",        columnList = "transfer_type"),
                @Index(name = "idx_transfer_transferred_at", columnList = "transferred_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseTransfer extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    /**
     * Type of entity being transferred.
     * Values: COURT, JUDGE, CLERK, BENCH
     */
    @NotBlank
    @Column(name = "transfer_type", nullable = false, length = 20)
    private String transferType;

    /** UUID of the entity before transfer (court/judge/clerk/bench UUID). */
    @Column(name = "from_entity_uuid", length = 36)
    private String fromEntityUuid;

    /** Display name of the source entity. */
    @Column(name = "from_entity_name", length = 300)
    private String fromEntityName;

    /** UUID of the entity after transfer. */
    @Column(name = "to_entity_uuid", length = 36)
    private String toEntityUuid;

    /** Display name of the destination entity. */
    @Column(name = "to_entity_name", length = 300)
    private String toEntityName;

    /** Reason for the transfer — required. */
    @NotBlank
    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    /** UUID of the admin or judge who initiated the transfer. */
    @Column(name = "transferred_by_uuid", length = 36)
    private String transferredByUuid;

    /** Role of the actor who initiated the transfer. */
    @Column(name = "transferred_by_role", length = 30)
    private String transferredByRole;

    @NotNull
    @Column(name = "transferred_at", nullable = false)
    private LocalDateTime transferredAt;
}
