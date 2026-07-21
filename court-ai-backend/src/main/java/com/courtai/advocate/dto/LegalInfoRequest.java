package com.courtai.advocate.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LegalInfoRequest {
    private String policeStation;
    private String firNumber;
    private String crimeNumber;
    private String previousCaseNumber;
    private String acts;
    private String sections;
    private String rules;
    private String articles;
    private String legalProvisions;
    private String precedentReferences;
}
