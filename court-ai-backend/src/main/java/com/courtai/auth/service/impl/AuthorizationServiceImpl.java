package com.courtai.auth.service.impl;

import com.courtai.auth.service.AuthorizationService;
import com.courtai.common.enums.PermissionCode;
import com.courtai.common.enums.UserRole;
import com.courtai.exception.UnauthorizedActionException;
import com.courtai.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Enterprise implementation of {@link AuthorizationService}.
 *
 * <p>All checks read the {@link UserPrincipal} from Spring Security's
 * {@link SecurityContextHolder} — no extra DB queries per request.
 * Compound checks compose the primitive checks for clean, readable logic.</p>
 *
 * <h3>Judicial Hierarchy Rules</h3>
 * <ul>
 *   <li><strong>ADMIN</strong> — full access to everything</li>
 *   <li><strong>JUDGE</strong> — only assigned cases / hearings / AI analysis</li>
 *   <li><strong>CLERK</strong> — only cases belonging to their court</li>
 *   <li><strong>ADVOCATE</strong> — only own cases / documents / notifications</li>
 * </ul>
 */
@Slf4j
@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    // =========================================================
    //  BASIC AUTHORITY CHECKS
    // =========================================================

    @Override
    public boolean hasPermission(PermissionCode code) {
        return currentAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals(code.name()));
    }

    @Override
    public boolean hasRole(UserRole role) {
        return currentAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals(role.name()));
    }

    @Override
    public void requirePermission(PermissionCode code) {
        if (!hasPermission(code)) {
            log.warn("Access denied — user [{}] lacks permission [{}]", getCurrentUserUuid(), code);
            throw new UnauthorizedActionException(
                    "You do not have the required permission: " + code.name());
        }
    }

    // =========================================================
    //  OWNERSHIP CHECKS
    // =========================================================

    @Override
    public boolean isOwner(String ownerUuid) {
        return Objects.equals(getCurrentUserUuid(), ownerUuid);
    }

    @Override
    public boolean isOwnerOrPrivileged(String ownerUuid) {
        return isOwner(ownerUuid)
                || hasRole(UserRole.ROLE_ADMIN)
                || hasRole(UserRole.ROLE_JUDGE)
                || hasRole(UserRole.ROLE_CLERK);
    }

    // =========================================================
    //  COURT ASSIGNMENT CHECKS
    // =========================================================

    @Override
    public boolean belongsToCourt(String courtUuid) {
        // ADMIN always passes
        if (hasRole(UserRole.ROLE_ADMIN)) return true;

        // Future: look up clerk_profiles / judge_profiles for courtUuid match
        // Returning true as placeholder until Court module is implemented.
        // Replace with:
        //   return profileRepository.existsByUserUuidAndCourtUuid(getCurrentUserUuid(), courtUuid);
        log.debug("belongsToCourt called — Court module not yet implemented, defaulting to true for non-admins.");
        return true;
    }

    // =========================================================
    //  CASE-LEVEL AUTHORIZATION
    // =========================================================

    @Override
    public boolean canViewCase(String caseOwnerUuid, String assignedJudgeUuid, String courtUuid) {
        if (hasRole(UserRole.ROLE_ADMIN)) return true;

        if (hasRole(UserRole.ROLE_JUDGE)) {
            // Judge can view all cases (VIEW_ALL_CASES) or must be assigned
            return hasPermission(PermissionCode.VIEW_ALL_CASES)
                    || Objects.equals(getCurrentUserUuid(), assignedJudgeUuid);
        }

        if (hasRole(UserRole.ROLE_CLERK)) {
            return belongsToCourt(courtUuid) && hasPermission(PermissionCode.VIEW_ALL_CASES);
        }

        if (hasRole(UserRole.ROLE_ADVOCATE)) {
            // Advocate can only view their own cases
            return isOwner(caseOwnerUuid) && hasPermission(PermissionCode.VIEW_CASE);
        }

        return false;
    }

    @Override
    public boolean canModifyCase(String caseOwnerUuid, String assignedJudgeUuid, String courtUuid) {
        if (hasRole(UserRole.ROLE_ADMIN)) return true;

        if (hasRole(UserRole.ROLE_JUDGE)) {
            return Objects.equals(getCurrentUserUuid(), assignedJudgeUuid)
                    && hasPermission(PermissionCode.UPDATE_CASE);
        }

        if (hasRole(UserRole.ROLE_CLERK)) {
            return belongsToCourt(courtUuid) && hasPermission(PermissionCode.UPDATE_CASE);
        }

        if (hasRole(UserRole.ROLE_ADVOCATE)) {
            return isOwner(caseOwnerUuid) && hasPermission(PermissionCode.UPDATE_CASE);
        }

        return false;
    }

    // =========================================================
    //  DOCUMENT AUTHORIZATION
    // =========================================================

    @Override
    public boolean canUploadDocument(String caseOwnerUuid) {
        if (hasRole(UserRole.ROLE_ADMIN)) return true;

        if (hasRole(UserRole.ROLE_CLERK)) {
            return hasPermission(PermissionCode.UPLOAD_DOCUMENT);
        }

        if (hasRole(UserRole.ROLE_ADVOCATE)) {
            return isOwner(caseOwnerUuid) && hasPermission(PermissionCode.UPLOAD_DOCUMENT);
        }

        // JUDGE cannot upload documents
        return false;
    }

    // =========================================================
    //  JUDICIAL WORKFLOW AUTHORIZATION
    // =========================================================

    @Override
    public boolean canAssignJudge() {
        return hasRole(UserRole.ROLE_ADMIN)
                || (hasRole(UserRole.ROLE_CLERK) && hasPermission(PermissionCode.ASSIGN_JUDGE));
    }

    @Override
    public boolean canGenerateOrder(String assignedJudgeUuid) {
        if (hasRole(UserRole.ROLE_ADMIN)) return true;

        return hasRole(UserRole.ROLE_JUDGE)
                && Objects.equals(getCurrentUserUuid(), assignedJudgeUuid)
                && hasPermission(PermissionCode.GENERATE_ORDER);
    }

    // =========================================================
    //  AI AUTHORIZATION (FUTURE-READY)
    // =========================================================

    @Override
    public boolean canViewAiAnalysis(String assignedJudgeUuid) {
        if (hasRole(UserRole.ROLE_ADMIN)) return true;

        // Future: AI Authorization Provider can add ML-based contextual checks here
        return hasRole(UserRole.ROLE_JUDGE)
                && Objects.equals(getCurrentUserUuid(), assignedJudgeUuid)
                && hasPermission(PermissionCode.VIEW_AI_ANALYSIS);
    }

    // =========================================================
    //  CURRENT USER HELPERS
    // =========================================================

    @Override
    public String getCurrentUserUuid() {
        Authentication auth = currentAuthentication();
        if (auth.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getUserUuid();
        }
        return null;
    }

    // =========================================================
    //  PRIVATE HELPERS
    // =========================================================

    private Authentication currentAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedActionException("No authenticated user found in security context.");
        }
        return auth;
    }
}
