package com.courtai.casemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseWizardDraftResponse {
    private String caseUuid;
    private String status;
    private String message;
}
