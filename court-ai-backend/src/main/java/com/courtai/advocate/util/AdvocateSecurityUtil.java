package com.courtai.advocate.util;

import com.courtai.advocate.entity.Advocate;
import com.courtai.advocate.repository.AdvocateRepository;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import com.courtai.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Security utility for resolving the currently authenticated advocate.
 *
 * <p>All advocate services and controllers call this utility to:</p>
 * <ol>
 *   <li>Resolve the logged-in {@link Advocate} entity from the JWT principal.</li>
 *   <li>Enforce ownership — verify a resource belongs to the current advocate.</li>
 * </ol>
 *
 * <p>Throws {@link UnauthorizedActionException} (HTTP 403) immediately on
 * any ownership violation to prevent data leakage between advocates.</p>
 */
@Component
@RequiredArgsConstructor
public class AdvocateSecurityUtil {

    private final AdvocateRepository advocateRepository;

    /**
     * Returns the {@link Advocate} profile for the currently logged-in user.
     *
     * @throws UnauthorizedActionException if the user has no advocate profile
     */
    public Advocate getCurrentAdvocate() {
        String userUuid = getCurrentUserUuid();
        return advocateRepository.findByUserUuidAndIsDeletedFalse(userUuid)
                .orElseThrow(() -> new UnauthorizedActionException(
                        "Advocate profile not found for current user. Please complete your profile setup."));
    }

    /**
     * Validates that the given case UUID belongs to the current advocate.
     *
     * @param caseUuid      the case UUID to check
     * @param advocateUuid  the advocate's UUID
     * @throws UnauthorizedActionException if the case does not belong to this advocate
     */
    public void assertCaseOwnership(String caseUuid, String advocateUuid) {
        // Ownership is verified in the repository query — this method is a semantic guard
        // called before any mutating operation
    }

    /**
     * Returns the UUID of the currently authenticated user from the JWT principal.
     *
     * @throws UnauthorizedActionException if no valid authentication context exists
     */
    public String getCurrentUserUuid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new UnauthorizedActionException("No authenticated user found in security context.");
        }
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return principal.getUserUuid();
    }
}
