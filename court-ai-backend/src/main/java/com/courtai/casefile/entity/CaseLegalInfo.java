package com.courtai.casefile.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "case_legal_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseLegalInfo extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false, unique = true)
    private CaseFile caseFile;

    @Column(name = "police_station", length = 200)
    private String policeStation;

    @Column(name = "fir_number", length = 100)
    private String firNumber;

    @Column(name = "crime_number", length = 100)
    private String crimeNumber;

    @Column(name = "previous_case_number", length = 100)
    private String previousCaseNumber;

    // We store these as JSON strings or comma separated text for simplicity in this version
    @Column(name = "acts", columnDefinition = "TEXT")
    private String acts;

    @Column(name = "sections", columnDefinition = "TEXT")
    private String sections;

    @Column(name = "rules", columnDefinition = "TEXT")
    private String rules;

    @Column(name = "articles", columnDefinition = "TEXT")
    private String articles;

    @Column(name = "legal_provisions", columnDefinition = "TEXT")
    private String legalProvisions;

    @Column(name = "precedent_references", columnDefinition = "TEXT")
    private String precedentReferences;
}
