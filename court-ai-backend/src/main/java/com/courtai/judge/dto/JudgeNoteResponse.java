package com.courtai.judge.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Private judge note response. Visible only to the owning judge.
 */
@Getter
@Builder
public class JudgeNoteResponse {

    private String uuid;
    private String caseUuid;
    private String noteText;
    private String noteType;
    private LocalDate noteDate;
    private Boolean isConfidential;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
