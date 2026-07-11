package com.courtai.advocate.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO for updating an advocate's own case.
 * Only mutable fields are included — status, judge, court are managed by clerk/admin.
 */
@Getter
@Setter
public class UpdateCaseRequest {

    @Size(max = 500)
    private String caseTitle;

    @Size(max = 5000)
    private String caseDescription;

    @Size(max = 200)
    private String policeStation;

    @Size(max = 500)
    private String actSection;

    @Size(max = 200)
    private String petitionerName;

    @Size(max = 200)
    private String respondentName;

    /** Advocate's response to a clerk objection. */
    @Size(max = 2000)
    private String clerkObjectionResponse;
}
