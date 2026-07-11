package com.courtai.casecategory.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * High-level category grouping for judicial cases.
 *
 * <p>CaseCategory is a lookup/reference entity managed by administrators.
 * It provides broad groupings (e.g., "Criminal", "Civil", "Family") that
 * can be used for filtering and reporting. The granular case classification
 * is determined by the {@link com.courtai.common.enums.CaseType} enum,
 * which remains the single source of truth for case typing.</p>
 */
@Entity
@Table(
        name = "case_categories",
        indexes = {
                @Index(name = "idx_case_cat_code",       columnList = "category_code"),
                @Index(name = "idx_case_cat_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseCategory extends BaseEntity {

    /** Unique short code for this category — e.g., "CRIM", "CIVIL", "FAM". */
    @NotBlank
    @Column(name = "category_code", nullable = false, unique = true, length = 20)
    private String categoryCode;

    /** Display name of the category — e.g., "Criminal", "Civil". */
    @NotBlank
    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    /** Optional description of the category scope. */
    @Column(name = "description", length = 500)
    private String description;

    /** Display order for UI rendering. */
    @Column(name = "display_order")
    private Integer displayOrder;

    /** Whether this category is currently available for selection. */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;
}
