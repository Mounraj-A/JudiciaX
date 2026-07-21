package com.courtai.master.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CaseTypeDto {
    private String uuid;
    private String typeCode;
    private String typeName;
    private String description;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
