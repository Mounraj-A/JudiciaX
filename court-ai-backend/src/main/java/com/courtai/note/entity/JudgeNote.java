package com.courtai.note.entity;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.common.entity.BaseEntity;
import com.courtai.judge.entity.Judge;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * Private notes recorded by a judge during case proceedings.
 *
 * <p>Judge notes are confidential by default and visible only to the
 * presiding judge and authorised court staff. They may capture observations,
 * instructions to clerks, or interim orders that are not yet formalised.</p>
 *
 * <p>Note types: OBSERVATION, INSTRUCTION, SUMMARY, INTERIM_ORDER, OTHER</p>
 */
@Entity
@Table(
        name = "judge_notes",
        indexes = {
                @Index(name = "idx_jnote_case_id",    columnList = "case_id"),
                @Index(name = "idx_jnote_judge_id",   columnList = "judge_id"),
                @Index(name = "idx_jnote_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JudgeNote extends BaseEntity {

    /** The case this note is associated with. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    /** The judge who wrote this note. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "judge_id", nullable = false)
    private Judge judge;

    /** The full text of the note. */
    @NotBlank
    @Column(name = "note_text", nullable = false, columnDefinition = "TEXT")
    private String noteText;

    /**
     * Categorisation of the note.
     * Values: OBSERVATION, INSTRUCTION, SUMMARY, INTERIM_ORDER, OTHER
     */
    @Column(name = "note_type", length = 30)
    @Builder.Default
    private String noteType = "OBSERVATION";

    /** Date on which this note was made. */
    @NotNull
    @Column(name = "note_date", nullable = false)
    private LocalDate noteDate;

    /**
     * Whether this note is restricted to the judge only.
     * Confidential notes are not visible to advocates or clerks.
     */
    @Column(name = "is_confidential", nullable = false)
    @Builder.Default
    private Boolean isConfidential = Boolean.TRUE;
}
