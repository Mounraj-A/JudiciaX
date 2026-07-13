package com.courtai.judge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request payload for creating or updating a private judge note.
 */
@Getter
@NoArgsConstructor
public class JudgeNoteRequest {

    @NotBlank(message = "Note text is required")
    @Size(min = 5, max = 10000, message = "Note text must be between 5 and 10000 characters")
    private String noteText;

    /**
     * Note category. Allowed values: OBSERVATION, INSTRUCTION, SUMMARY, INTERIM_ORDER, OTHER.
     * Defaults to OBSERVATION if not provided.
     */
    @Size(max = 30, message = "Note type must not exceed 30 characters")
    private String noteType;

    /**
     * Whether this note is strictly confidential to the judge.
     * Defaults to true.
     */
    private Boolean isConfidential;
}
