package com.courtai.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Request to assign a judge to a case. */
@Getter
@NoArgsConstructor
public class AssignJudgeRequest {

    @NotBlank(message = "Case UUID is required")
    private String caseUuid;

    @NotBlank(message = "Judge UUID (user UUID) is required")
    private String judgeUserUuid;

    private String reason;
}
