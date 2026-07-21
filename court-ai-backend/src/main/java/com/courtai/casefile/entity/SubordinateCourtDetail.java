package com.courtai.casefile.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "subordinate_court_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubordinateCourtDetail extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    @Column(name = "subordinate_court", length = 200)
    private String subordinateCourt;

    @Column(name = "judge_name", length = 200)
    private String judgeName;

    @Column(name = "cnr_number", length = 50)
    private String cnrNumber;

    @Column(name = "case_number", length = 50)
    private String caseNumber;

    @Column(name = "year")
    private Integer year;

    @Column(name = "judgment_date")
    private LocalDate judgmentDate;

    @Column(name = "impugned_order_date")
    private LocalDate impugnedOrderDate;

    @Column(name = "cc_applied_date")
    private LocalDate ccAppliedDate;

    @Column(name = "cc_ready_date")
    private LocalDate ccReadyDate;
}
