package com.courtai.clerk.util;

import com.courtai.clerk.entity.Clerk;
import com.courtai.clerk.repository.ClerkRepository;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import com.courtai.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility bean that resolves the currently authenticated clerk from the
 * Spring Security context and enforces court-level access control.
 *
 * <p>All clerk service methods must call {@link #getCurrentClerk()} to
 * obtain the authenticated clerk before performing any business logic.</p>
 */
@Component
@RequiredArgsConstructor
public class ClerkSecurityUtil {

    private final ClerkRepository clerkRepository;

    /**
     * Resolves the authenticated clerk from the JWT principal.
     *
     * @throws UnauthorizedActionException if no valid clerk profile is found
     */
    public Clerk getCurrentClerk() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new UnauthorizedActionException("No authenticated clerk found in security context.");
        }

        String userUuid = principal.getUserUuid();
        return clerkRepository.findByUserUuidAndIsDeletedFalse(userUuid)
                .orElseThrow(() -> new UnauthorizedActionException(
                        "No active Clerk profile found for user: " + userUuid));
    }

    /**
     * Validates that the clerk's assigned court matches the given court ID.
     * Used to prevent a clerk from accessing cases in another court.
     *
     * @throws UnauthorizedActionException if the clerk is not assigned to the court
     */
    public void assertCourtAccess(Long courtId) {
        Clerk clerk = getCurrentClerk();
        if (clerk.getCourt() == null) {
            throw new UnauthorizedActionException(
                    "Your Clerk profile has no court assignment. Contact admin.");
        }
        if (!clerk.getCourt().getId().equals(courtId)) {
            throw new UnauthorizedActionException(
                    "You are not authorised to access cases from court ID: " + courtId);
        }
    }

    /**
     * Returns the court ID of the current clerk.
     *
     * @throws UnauthorizedActionException if the clerk has no court assigned
     */
    public Long getCurrentCourtId() {
        Clerk clerk = getCurrentClerk();
        if (clerk.getCourt() == null) {
            throw new UnauthorizedActionException(
                    "Your Clerk profile has no court assignment. Contact admin.");
        }
        return clerk.getCourt().getId();
    }
}
