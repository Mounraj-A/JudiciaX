package com.courtai.casemanagement.dto;

import com.courtai.common.enums.CasePriority;
import com.courtai.common.enums.CaseStatus;
import com.courtai.common.enums.CaseType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Advanced search / filter request supporting all searchable dimensions.
 * All fields are optional — only non-null fields are applied as filters.
 */
@Getter
@NoArgsConstructor
public class CaseSearchRequest {

    // ── Identifiers ───────────────────────────────────────────────────────────
    private String caseNumber;
    private String caseUuid;
    private String cnrNumber;
    private String officialCaseNumber;

    // ── Parties ───────────────────────────────────────────────────────────────
    private String petitionerName;
    private String respondentName;

    // ── Assignments ───────────────────────────────────────────────────────────
    private String advocateUuid;
    private String judgeUuid;
    private String clerkUuid;

    // ── Court & Bench ─────────────────────────────────────────────────────────
    private String courtUuid;
    private String benchUuid;
    private String district;
    private String state;

    // ── Classification ────────────────────────────────────────────────────────
    private CaseType caseType;
    private String categoryUuid;
    private CasePriority priority;
    private CaseStatus status;

    // ── Date Range ────────────────────────────────────────────────────────────
    private Integer filingYear;
    private LocalDate filingDateFrom;
    private LocalDate filingDateTo;

    // ── AI & Flags ────────────────────────────────────────────────────────────
    private Double minPriorityScore;
    private Boolean aiPending;
    private Boolean highPriority;

    // ── Keyword ───────────────────────────────────────────────────────────────
    private String keyword;

    // ── Pagination ────────────────────────────────────────────────────────────
    private int page   = 0;
    private int size   = 20;
    private String sortBy        = "createdAt";
    private String sortDirection = "desc";
}
