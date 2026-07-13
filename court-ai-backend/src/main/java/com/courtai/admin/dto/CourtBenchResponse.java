package com.courtai.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CourtBenchResponse {
    private String uuid;
    private String courtUuid;
    private String courtName;
    private String benchNumber;
    private String benchType;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
