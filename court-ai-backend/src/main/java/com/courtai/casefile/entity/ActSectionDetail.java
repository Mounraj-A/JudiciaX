package com.courtai.casefile.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "act_section_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActSectionDetail extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    @Column(name = "act_name", length = 300)
    private String actName;

    @Column(name = "section", length = 100)
    private String section;

    @Column(name = "rule_info", length = 100)
    private String ruleInfo;

    @Column(name = "article", length = 100)
    private String article;
}
