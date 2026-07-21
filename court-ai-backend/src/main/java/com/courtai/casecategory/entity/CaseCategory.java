package com.courtai.casecategory.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.master.entity.CaseType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Granular case category (e.g., Property Dispute, Murder) linked to a parent CaseType.
 */
@Entity
@Table(
        name = "case_categories",
        indexes = {
                @Index(name = "idx_case_cat_code",       columnList = "category_code"),
                @Index(name = "idx_case_cat_is_deleted", columnList = "is_deleted"),
                @Index(name = "idx_case_cat_type_id",    columnList = "case_type_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseCategory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_type_id", nullable = false)
    private CaseType caseType;

    @NotBlank
    @Column(name = "category_code", nullable = false, unique = true, length = 20)
    private String categoryCode;

    @NotBlank
    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;
}
