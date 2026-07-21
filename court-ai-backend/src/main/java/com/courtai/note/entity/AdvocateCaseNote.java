package com.courtai.note.entity;

import com.courtai.advocate.entity.Advocate;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(
        name = "advocate_case_notes",
        indexes = {
                @Index(name = "idx_anote_case_id", columnList = "case_id"),
                @Index(name = "idx_anote_advocate_id", columnList = "advocate_id"),
                @Index(name = "idx_anote_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvocateCaseNote extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advocate_id", nullable = false)
    private Advocate advocate;

    @NotBlank
    @Column(name = "note_title", nullable = false, length = 300)
    private String noteTitle;

    @NotBlank
    @Column(name = "note_content", columnDefinition = "TEXT", nullable = false)
    private String noteContent;

}
