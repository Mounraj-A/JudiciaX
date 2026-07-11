package com.courtai.court.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Represents a judicial bench within a {@link Court}.
 *
 * <p>A bench can be SINGLE (one judge), DIVISION (two judges),
 * FULL (three or more judges), or SPECIAL (constitution bench).</p>
 */
@Entity
@Table(
        name = "court_benches",
        indexes = {
                @Index(name = "idx_bench_court_id",   columnList = "court_id"),
                @Index(name = "idx_bench_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtBench extends BaseEntity {

    /** The court this bench belongs to. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    /** Bench identifier within the court — e.g., "BENCH-1", "BENCH-A". */
    @NotBlank
    @Column(name = "bench_number", nullable = false, length = 20)
    private String benchNumber;

    /**
     * Type of bench composition.
     * Values: SINGLE, DIVISION, FULL, SPECIAL
     */
    @Column(name = "bench_type", length = 30)
    private String benchType;

    /** Brief description or specialization of this bench. */
    @Column(name = "description", length = 500)
    private String description;

    /** Whether this bench is currently active. */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;
}
