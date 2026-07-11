package com.courtai.advocate.service;

import com.courtai.advocate.dto.CaseResponse;
import com.courtai.advocate.dto.CaseSummaryResponse;
import com.courtai.advocate.dto.CreateCaseRequest;
import com.courtai.advocate.dto.UpdateCaseRequest;
import com.courtai.common.enums.CaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service contract for advocate case management.
 * Every operation is scoped to the currently logged-in advocate's own cases.
 */
public interface AdvocateCaseService {

    /** File a new case. Case number is auto-generated. */
    CaseResponse createCase(CreateCaseRequest request);

    /** Returns all cases belonging to the current advocate. */
    Page<CaseSummaryResponse> getMyCases(Pageable pageable);

    /** Returns a specific case — validates ownership. */
    CaseResponse getCaseByUuid(String caseUuid);

    /** Updates mutable fields of a case — validates ownership. */
    CaseResponse updateCase(String caseUuid, UpdateCaseRequest request);

    /** Returns cases filtered by status. */
    Page<CaseSummaryResponse> getCasesByStatus(CaseStatus status, Pageable pageable);

    /** Keyword search across title and party names. */
    Page<CaseSummaryResponse> searchCases(String keyword, Pageable pageable);
}
