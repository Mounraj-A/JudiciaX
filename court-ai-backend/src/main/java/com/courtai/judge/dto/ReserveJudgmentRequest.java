package com.courtai.judge.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request payload for reserving judgment on a case.
 * Sets case status to JUDGEMENT_RESERVED.
 */
@Getter
@NoArgsConstructor
public class ReserveJudgmentRequest {

    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    private String reason;
}
