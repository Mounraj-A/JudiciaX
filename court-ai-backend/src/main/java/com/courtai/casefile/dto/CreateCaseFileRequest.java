package com.courtai.casefile.dto;

import com.courtai.common.enums.CasePriority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Request DTO for filing a new case.
 */
@Getter
@Setter
@Builder
public class CreateCaseFileRequest {

    @NotBlank(message = "Case title is required")
    @Size(max = 500, message = "Case title must not exceed 500 characters")
    private String caseTitle;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String caseDescription;

    @NotNull(message = "Case type is required")
    private String caseType;

    @NotBlank(message = "Petitioner name is required")
    @Size(max = 200)
    private String petitionerName;

    @NotBlank(message = "Respondent name is required")
    @Size(max = 200)
    private String respondentName;

    private LocalDate filingDate;

    /** UUID of the court where the case is being filed. */
    private String courtUuid;

    /** UUID of the case category. */
    private String caseCategoryUuid;

    /** UUID of the petitioner's advocate. */
    private String petitionerAdvocateUuid;

    /** UUID of the respondent's advocate. */
    private String respondentAdvocateUuid;

    @Size(max = 200)
    private String policeStation;

    @Size(max = 500)
    private String actSection;

    @Builder.Default
    private CasePriority priority = CasePriority.LOW;
}
