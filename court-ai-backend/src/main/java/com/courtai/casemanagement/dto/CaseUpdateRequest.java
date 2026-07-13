package com.courtai.casemanagement.dto;

import com.courtai.common.enums.CasePriority;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** Request DTO for updating an existing draft case (DRAFT status only for advocates). */
@Getter
@NoArgsConstructor
public class CaseUpdateRequest {

    @Size(max = 500)
    private String caseTitle;

    @Size(max = 5000)
    private String caseDescription;

    @Size(max = 200)
    private String petitionerName;

    @Size(max = 200)
    private String respondentName;

    private LocalDate filingDate;

    @Size(max = 200)
    private String policeStation;

    @Size(max = 500)
    private String actSection;

    private CasePriority priority;

    // ── Case Flags (partial update) ───────────────────────────────────────────
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
