package com.courtai.casemanagement.service;

import com.courtai.casemanagement.dto.CaseTimelineResponse;
import com.courtai.casefile.entity.CaseFile;

import java.util.List;

/** Service for building and retrieving the ordered lifecycle timeline of a case. */
public interface CaseTimelineService {

    /**
     * Records a new lifecycle event on the given case.
     *
     * @param caseFile  the case to record against
     * @param eventType machine-readable event code (e.g., CASE_CREATED, SUBMITTED)
     * @param eventLabel human-readable label (e.g., "Case Submitted")
     * @param actorUuid UUID of the triggering user
     * @param actorRole role of the actor
     * @param actorName display name of the actor
     * @param reason    optional reason or remarks
     */
    void recordEvent(CaseFile caseFile, String eventType, String eventLabel,
                     String actorUuid, String actorRole, String actorName, String reason);

    /**
     * Returns the complete ordered timeline for a case.
     */
    List<CaseTimelineResponse> getTimeline(String caseUuid);
}
