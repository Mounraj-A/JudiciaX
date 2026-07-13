package com.courtai.casemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request DTO for all assignment operations: judge, clerk, bench.
 * The {@code assignmentType} field discriminates the action.
 */
@Getter
@NoArgsConstructor
public class CaseAssignmentRequest {

    /**
     * Type of assignment: JUDGE, CLERK, BENCH
     */
    @NotBlank(message = "Assignment type is required")
    private String assignmentType;

    /** UUID of the entity being assigned (judge/clerk/bench userUuid). */
    @NotBlank(message = "Assignee UUID is required")
    private String assigneeUuid;

    /** Optional reason for the assignment. */
    private String reason;
}
