package com.courtai.clerk.service;

import com.courtai.clerk.dto.*;
import com.courtai.common.enums.CaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service contract for clerk case scrutiny operations.
 *
 * <p>Every method enforces that the case belongs to the clerk's assigned court.</p>
 */
public interface ClerkCaseService {

    /** Open a submitted case for scrutiny — transitions to UNDER_SCRUTINY. */
    CaseScrutinyResponse openScrutiny(String caseUuid, String remarks);

    /** Get full scrutiny detail for one case. */
    CaseScrutinyResponse getCaseDetail(String caseUuid);

    /** List cases with specific statuses, scoped to clerk's court. */
    Page<ClerkCaseSummaryResponse> getCases(List<CaseStatus> statuses, Pageable pageable);

    /** Keyword search within clerk's court. */
    Page<ClerkCaseSummaryResponse> searchCases(String keyword, Pageable pageable);

    /** Raise a formal objection and return the case to the advocate. */
    ObjectionResponse raiseObjection(String caseUuid, RaiseObjectionRequest request);

    /** Get all objections for a case. */
    List<ObjectionResponse> getObjections(String caseUuid);

    /** Mark the case as verified (passes all checks, ready to register). */
    CaseScrutinyResponse verifyCase(String caseUuid, ScrutinyActionRequest request);

    /** Return case to advocate without registering. */
    CaseScrutinyResponse returnCase(String caseUuid, ScrutinyActionRequest request);

    /** Get status timeline for a case. */
    List<CaseTimelineResponse> getCaseTimeline(String caseUuid);
}
