package com.courtai.judge.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request payload for adjourning a hearing to a future date.
 */
@Getter
@NoArgsConstructor
public class AdjournHearingRequest {

    @NotBlank(message = "Adjournment reason is required")
    private String adjournReason;

    @FutureOrPresent(message = "Next hearing date must be today or in the future")
    private LocalDate nextHearingDate;
}
