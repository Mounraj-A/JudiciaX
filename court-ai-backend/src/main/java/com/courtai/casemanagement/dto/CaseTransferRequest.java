package com.courtai.casemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request DTO for all transfer operations: COURT, JUDGE, CLERK, BENCH.
 * The {@code transferType} field discriminates the operation.
 */
@Getter
@NoArgsConstructor
public class CaseTransferRequest {

    /**
     * Type of transfer: COURT, JUDGE, CLERK, BENCH
     */
    @NotBlank(message = "Transfer type is required")
    private String transferType;

    /** UUID of the target entity (new court/judge/clerk/bench UUID). */
    @NotBlank(message = "Target UUID is required")
    private String targetUuid;

    /** Mandatory reason for the transfer. */
    @NotBlank(message = "Transfer reason is required")
    private String reason;
}
