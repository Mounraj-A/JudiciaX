package com.courtai.auth.service;

import com.courtai.common.enums.PermissionCode;
import com.courtai.common.enums.UserRole;

/**
 * Enterprise Authorization Service for the Judicial Case Management System.
 *
 * <p>Provides programmatic authorization checks beyond simple {@code @PreAuthorize}
 * annotations — including ownership validation, court-assignment checks, and
 * compound judicial-hierarchy guards.</p>
 *
 * <p>Designed for future AI authorization integration: the {@code canViewAiAnalysis}
 * and {@code canModifyPriority} methods accept entity UUIDs and can be enhanced
 * with AI-model permission decisions without changing call sites.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * // In a service method:
 * authorizationService.requirePermission(PermissionCode.CREATE_CASE);
 *
 * // Ownership check before returning data:
 * if (!authorizationService.isOwner(caseOwnerUuid)) {
 *     throw new UnauthorizedActionException("You may only access your own cases.");
 * }
 * }</pre>
 */
public interface AuthorizationService {

    // ── BASIC AUTHORITY CHECKS ───────────────────────────────────────────────

    /**
     * Returns {@code true} if the current authenticated user holds the given permission.
     *
     * @param code the permission to check
     */
    boolean hasPermission(PermissionCode code);

    /**
     * Returns {@code true} if the current authenticated user holds the given role.
     *
     * @param role the role to check
     */
    boolean hasRole(UserRole role);

    /**
     * Throws {@link com.courtai.exception.UnauthorizedActionException} if the current
     * user does not hold the given permission.
     */
    void requirePermission(PermissionCode code);

    // ── OWNERSHIP CHECKS ─────────────────────────────────────────────────────

    /**
     * Returns {@code true} if the current user's UUID matches {@code ownerUuid}.
     *
     * <p>Used to ensure Advocates can only access their own cases / documents.</p>
     */
    boolean isOwner(String ownerUuid);

    /**
     * Returns {@code true} if the current user is either the owner or holds
     * a privileged role (ADMIN, JUDGE, CLERK) that can bypass ownership restrictions.
     */
    boolean isOwnerOrPrivileged(String ownerUuid);

    // ── COURT ASSIGNMENT CHECKS ───────────────────────────────────────────────

    /**
     * Returns {@code true} if the current user (Clerk / Judge) belongs to the
     * court identified by {@code courtUuid}.
     *
     * <p>Implementation note: delegates to the profile table lookup.
     * Always returns {@code true} for ROLE_ADMIN.</p>
     */
    boolean belongsToCourt(String courtUuid);

    // ── CASE-LEVEL AUTHORIZATION ──────────────────────────────────────────────

    /**
     * Compound check: can the current user view the case?
     *
     * <ul>
     *   <li>ADMIN → always yes</li>
     *   <li>JUDGE → must be assigned judge of the case (or VIEW_ALL_CASES permission)</li>
     *   <li>CLERK → must belong to the court that owns the case</li>
     *   <li>ADVOCATE → must be the filing advocate (ownership)</li>
     * </ul>
     *
     * @param caseOwnerUuid  UUID of the advocate who filed the case
     * @param assignedJudgeUuid UUID of the judge assigned to the case (nullable)
     * @param courtUuid      UUID of the court handling the case
     */
    boolean canViewCase(String caseOwnerUuid, String assignedJudgeUuid, String courtUuid);

    /**
     * Compound check: can the current user modify (update/close/reopen) the case?
     *
     * <ul>
     *   <li>ADMIN → always yes</li>
     *   <li>JUDGE → must be assigned judge + hold UPDATE_CASE</li>
     *   <li>CLERK → must belong to court + hold UPDATE_CASE</li>
     *   <li>ADVOCATE → must be owner + hold UPDATE_CASE (only own cases)</li>
     * </ul>
     */
    boolean canModifyCase(String caseOwnerUuid, String assignedJudgeUuid, String courtUuid);

    // ── DOCUMENT AUTHORIZATION ────────────────────────────────────────────────

    /**
     * Returns {@code true} if the current user can upload a document to the case.
     *
     * <ul>
     *   <li>ADMIN, CLERK → always yes (with UPLOAD_DOCUMENT permission)</li>
     *   <li>ADVOCATE → must be case owner + hold UPLOAD_DOCUMENT</li>
     *   <li>JUDGE → not allowed to upload documents (read-only)</li>
     * </ul>
     */
    boolean canUploadDocument(String caseOwnerUuid);

    // ── JUDICIAL WORKFLOW AUTHORIZATION ──────────────────────────────────────

    /**
     * Returns {@code true} if the current user can assign a judge to a case.
     *
     * <p>Only ROLE_CLERK with ASSIGN_JUDGE or ROLE_ADMIN.</p>
     */
    boolean canAssignJudge();

    /**
     * Returns {@code true} if the current user can generate a court order for the case.
     *
     * <p>Only ROLE_JUDGE with GENERATE_ORDER assigned to them, or ROLE_ADMIN.</p>
     *
     * @param assignedJudgeUuid UUID of the judge assigned to the case
     */
    boolean canGenerateOrder(String assignedJudgeUuid);

    // ── AI AUTHORIZATION (FUTURE-READY) ──────────────────────────────────────

    /**
     * Returns {@code true} if the current user can view AI-generated analysis for the case.
     *
     * <p>Designed as an extension point: a future AI Authorization Provider can
     * override this to add ML-based contextual checks.</p>
     */
    boolean canViewAiAnalysis(String assignedJudgeUuid);

    /**
     * Returns the current authenticated user's UUID.
     */
    String getCurrentUserUuid();
}
