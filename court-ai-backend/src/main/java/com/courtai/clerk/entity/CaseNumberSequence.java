package com.courtai.clerk.entity;

import com.courtai.court.entity.Court;
import jakarta.persistence.*;
import lombok.*;

/**
 * Sequence counter for official case number generation.
 *
 * <p>One row exists per (court, year) pair. The {@code lastSeq} field is
 * incremented atomically via a SELECT FOR UPDATE lock in
 * {@code CaseNumberGeneratorService} to guarantee uniqueness even under
 * concurrent registration requests.</p>
 */
@Entity
@Table(
        name = "case_number_sequences",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_case_number_seq_court_year",
                columnNames = {"court_id", "year"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseNumberSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "last_seq", nullable = false)
    @Builder.Default
    private Integer lastSeq = 0;
}
