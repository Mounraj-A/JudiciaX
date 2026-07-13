package com.courtai.casemanagement.service;

import com.courtai.common.enums.CaseStatus;

import java.util.List;

/**
 * Core workflow engine — enforces the judicial case state machine.
 *
 * <p>All status transitions in the system MUST flow through this service.
 * No other service may directly set {@code CaseFile.status}.</p>
 */
public interface CaseWorkflowService {

    /**
     * Returns the list of statuses that a case may legally transition to
     * from the given {@code current} status.
     */
    List<CaseStatus> getAllowedTransitions(CaseStatus current);

    /**
     * Validates whether transitioning from {@code from} to {@code to} is permitted.
     *
     * @throws com.courtai.exception.BusinessRuleViolationException if the transition is invalid
     */
    void validateTransition(CaseStatus from, CaseStatus to);

    /**
     * Executes a validated status transition on the given case.
     * Persists the new status, records status history, records a timeline event,
     * and logs the audit trail.
     *
     * @param caseUuid  UUID of the case to transition
     * @param toStatus  target status
     * @param actorUuid UUID of the user performing the transition
     * @param actorRole role of the actor
     * @param reason    optional reason / remarks
     * @return the updated case UUID
     */
    String executeTransition(String caseUuid, CaseStatus toStatus,
                             String actorUuid, String actorRole, String reason);
}
