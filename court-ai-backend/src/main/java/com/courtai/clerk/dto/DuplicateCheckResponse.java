package com.courtai.clerk.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/** Result of the duplicate detection check performed before case registration. */
@Data
@Builder
public class DuplicateCheckResponse {
    private String caseUuid;
    private String caseNumber;
    private boolean duplicatesFound;
    private int duplicateCount;
    private List<DuplicateCaseSummary> potentialDuplicates;
    private String message;

    @Data
    @Builder
    public static class DuplicateCaseSummary {
        private String uuid;
        private String caseNumber;
        private String officialCaseNumber;
        private String caseTitle;
        private String petitionerName;
        private String respondentName;
        private String status;
        private String filingDate;
    }
}
