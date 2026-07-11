package com.courtai.clerk.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Request body for evidence verification or rejection by clerk. */
@Data
public class VerifyEvidenceRequest {

    /** Whether the evidence is approved (true) or rejected (false). */
    private boolean approved;

    @NotBlank(message = "Remarks are required")
    private String remarks;

    private String rejectionReason;
}
