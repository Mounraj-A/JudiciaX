package com.courtai.judge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request payload for disposing (closing) a case after judgment.
 */
@Getter
@NoArgsConstructor
public class DisposeRequest {

    @NotBlank(message = "Disposal reason is required")
    @Size(max = 1000, message = "Disposal reason must not exceed 1000 characters")
    private String disposalReason;

    /** UUID of the final judgment order, if already uploaded. */
    private String judgmentOrderUuid;
}
