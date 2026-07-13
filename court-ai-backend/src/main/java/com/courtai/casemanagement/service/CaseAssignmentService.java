package com.courtai.casemanagement.service;

import com.courtai.casemanagement.dto.CaseAssignmentRequest;
import com.courtai.casemanagement.dto.CaseTransferRequest;

/**
 * Service for all case assignment and transfer operations.
 *
 * <p>Every assignment and transfer records a {@link com.courtai.casemanagement.entity.CaseTimeline}
 * event, a {@link com.courtai.casemanagement.entity.CaseTransfer} record,
 * an audit log entry, and triggers notifications.</p>
 */
public interface CaseAssignmentService {

    /** Assign a judge to a case. Validates case is REGISTERED or above. */
    void assignJudge(String caseUuid, CaseAssignmentRequest request,
                     String actorUuid, String actorRole);

    /** Transfer judge from one case/assignment to another. */
    void transferJudge(String caseUuid, CaseTransferRequest request,
                       String actorUuid, String actorRole);

    /** Assign a clerk to handle scrutiny for a case. */
    void assignClerk(String caseUuid, CaseAssignmentRequest request,
                     String actorUuid, String actorRole);

    /** Transfer a case's scrutiny clerk. */
    void transferClerk(String caseUuid, CaseTransferRequest request,
                       String actorUuid, String actorRole);

    /** Transfer a case from one court to another. */
    void transferCourt(String caseUuid, CaseTransferRequest request,
                       String actorUuid, String actorRole);

    /** Assign the case to a specific bench within its current court. */
    void assignBench(String caseUuid, CaseAssignmentRequest request,
                     String actorUuid, String actorRole);
}
