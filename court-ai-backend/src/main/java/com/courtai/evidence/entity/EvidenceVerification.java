package com.courtai.evidence.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Records the verification decision for a piece of {@link Evidence}.
 *
 * <p>One-to-one with {@link Evidence}. Created when a clerk or judge
 * formally reviews and rules on the admissibility of an evidence item.
 * Verification is an immutable judicial record once set to VERIFIED or REJECTED.</p>
 */
@Entity
@Table(
        name = "evidence_verifications",
        indexes = {
                @Index(name = "idx_evver_evidence_id",  columnList = "evidence_id"),
                @Index(name = "idx_evver_status",       columnList = "verification_status"),
                @Index(name = "idx_evver_is_deleted",   columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvidenceVerification extends BaseEntity {

    /** The evidence item being verified. */
    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evidence_id", nullable = false, unique = true)
    private Evidence evidence;

    /**
     * Current verification status.
     * Values: PENDING, VERIFIED, REJECTED, UNDER_REVIEW
     */
    @Column(name = "verification_status", nullable = false, length = 20)
    @Builder.Default
    private String verificationStatus = "PENDING";

    /** UUID of the judge or clerk who performed the verification. */
    @Column(name = "verified_by_uuid", length = 36)
    private String verifiedByUuid;

    /** Role of the verifier — ROLE_JUDGE, ROLE_CLERK. */
    @Column(name = "verified_by_role", length = 30)
    private String verifiedByRole;

    /** Timestamp when the verification decision was made. */
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    /** Detailed remarks justifying the verification decision. */
    @Column(name = "remarks", length = 1000)
    private String remarks;
}
