package com.courtai.casemanagement.dto;

import com.courtai.common.enums.CasePriority;
import com.courtai.common.enums.CaseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** Request DTO for creating a new judicial case. */
@Getter
@NoArgsConstructor
public class CaseCreateRequest {

    @NotBlank(message = "Case title is required")
    @Size(max = 500)
    private String caseTitle;

    @Size(max = 5000)
    private String caseDescription;

    @NotNull(message = "Case type is required")
    private CaseType caseType;

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

    @Size(max = 200)
    private String policeStation;

    @Size(max = 500)
    private String actSection;

    private CasePriority priority;

    // ── Case Flags ────────────────────────────────────────────────────────────
    private Boolean medicalEmergency;
    private Boolean childInvolved;
    private Boolean womenSafety;
    private Boolean seniorCitizen;
    private Boolean disability;
    private Boolean financialFraud;
    private Boolean cyberCrime;
    private Boolean threatToLife;
    private Boolean highPublicInterest;
}
