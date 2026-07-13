package com.courtai.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Request for creating or updating a court bench. */
@Getter
@NoArgsConstructor
public class CourtBenchRequest {

    @NotBlank(message = "Court UUID is required")
    private String courtUuid;

    @NotBlank(message = "Bench number is required")
    private String benchNumber;

    /** e.g. SINGLE, DIVISION, FULL, SPECIAL */
    private String benchType;
    private String description;
    private Boolean isActive;
}
