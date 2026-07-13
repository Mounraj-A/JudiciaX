package com.courtai.casemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Request to archive a DISPOSED or CLOSED case. */
@Getter
@NoArgsConstructor
public class CaseArchiveRequest {

    @NotBlank(message = "Archive reason is required")
    private String reason;
}
