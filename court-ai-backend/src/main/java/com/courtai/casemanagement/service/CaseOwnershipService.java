package com.courtai.casemanagement.service;

/**
 * Validates and enforces case ownership and assignment rules.
 *
 * <p>All ownership checks should be called before performing
 * role-sensitive operations to prevent horizontal privilege escalation.</p>
 */
public interface CaseOwnershipService {

    /**
     * Verifies the given advocate UUID has ownership of (is petitioner or respondent advocate on)
     * the specified case.
     *
     * @throws com.courtai.exception.UnauthorizedActionException if not owner
     */
    void verifyAdvocateOwnership(String caseUuid, String advocateUuid);

    /**
     * Verifies the given judge UUID is currently assigned to the specified case.
     *
     * @throws com.courtai.exception.UnauthorizedActionException if not assigned
     */
    void verifyJudgeAssignment(String caseUuid, String judgeUserUuid);

    /**
     * Verifies the given clerk's assigned court matches the case's court.
     *
     * @throws com.courtai.exception.UnauthorizedActionException if mismatch
     */
    void verifyClerkCourt(String caseUuid, String clerkUserUuid);

    /**
     * Returns true if the actor is an admin (always permitted to view, never to decide).
     */
    boolean isAdmin(String actorRole);

    /**
     * Returns true if the case UUID is owned by or assigned to the given actor.
     * Convenience method combining ownership + assignment checks by role.
     */
    boolean hasAccessToCase(String caseUuid, String actorUuid, String actorRole);
}
