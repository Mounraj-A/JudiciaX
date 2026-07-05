package com.courtai.casefile.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.CaseStatus;
import com.courtai.common.enums.CaseType;
import com.courtai.judge.entity.Judge;
import com.courtai.advocate.entity.Advocate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

/**
 * Core Case File entity representing a judicial case in the system.
 *
 * <p>This is the central aggregate of the judicial case management system.
 * AI prioritization scores and scheduling logic will be added in Phase 2.</p>
 */
@Entity
@Table(
        name = "case_files",
        indexes = {
                @Index(name = "idx_case_number", columnList = "case_number"),
                @Index(name = "idx_case_status", columnList = "status"),
                @Index(name = "idx_case_judge_id", columnList = "assigned_judge_id"),
                @Index(name = "idx_case_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseFile extends BaseEntity {

    @NotBlank
    @Column(name = "case_number", nullable = false, unique = true, length = 50)
    private String caseNumber;

    @NotBlank
    @Column(name = "case_title", nullable = false, length = 500)
    private String caseTitle;

    @Column(name = "case_description", columnDefinition = "TEXT")
    private String caseDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "case_type", nullable = false, length = 50)
    private CaseType caseType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private CaseStatus status = CaseStatus.FILED;

    @Column(name = "filing_date")
    private LocalDate filingDate;

    @Column(name = "hearing_date")
    private LocalDate hearingDate;

    @Column(name = "priority_score")
    private Double priorityScore;  // AI-computed score — populated in Phase 2

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_judge_id")
    private Judge assignedJudge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petitioner_advocate_id")
    private Advocate petitionerAdvocate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "respondent_advocate_id")
    private Advocate respondentAdvocate;

    @Column(name = "petitioner_name", length = 200)
    private String petitionerName;

    @Column(name = "respondent_name", length = 200)
    private String respondentName;

    @Column(name = "court_name", length = 200)
    private String courtName;
}
