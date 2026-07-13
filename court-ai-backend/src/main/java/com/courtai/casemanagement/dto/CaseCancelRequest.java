package com.courtai.casemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Request to cancel a case before registration. */
@Getter
@NoArgsConstructor
public class CaseCancelRequest {

    @NotBlank(message = "Cancellation reason is required")
    private String reason;
}
