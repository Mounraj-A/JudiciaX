package com.courtai.casemanagement.dto;

import lombok.Builder;
import lombok.Getter;

/** Response after cloning a case. */
@Getter
@Builder
public class CaseCloneResponse {
    private String originalCaseUuid;
    private String originalCaseNumber;
    private String clonedCaseUuid;
    private String clonedCaseNumber;
    private String message;
}
