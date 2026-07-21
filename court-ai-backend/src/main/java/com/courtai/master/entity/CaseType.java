package com.courtai.master.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Master entity representing a high-level case type (e.g., Civil, Criminal).
 */
@Entity
@Table(
        name = "case_types",
        indexes = {
                @Index(name = "idx_case_types_code",       columnList = "type_code"),
                @Index(name = "idx_case_types_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseType extends BaseEntity {

    @NotBlank
    @Column(name = "type_code", nullable = false, unique = true, length = 20)
    private String typeCode;

    @NotBlank
    @Column(name = "type_name", nullable = false, length = 100)
    private String typeName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;
}
