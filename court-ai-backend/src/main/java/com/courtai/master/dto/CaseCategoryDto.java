package com.courtai.master.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CaseCategoryDto {
    private String uuid;
    private String caseTypeUuid;
    private String caseTypeCode;
    private String categoryCode;
    private String categoryName;
    private String description;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
