package com.courtai.casemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Request to clone a DRAFT case. */
@Getter
@NoArgsConstructor
public class CaseCloneRequest {

    @NotBlank(message = "New case title is required")
    private String newCaseTitle;

    /** Optional additional notes for the cloned case. */
    private String notes;
}
