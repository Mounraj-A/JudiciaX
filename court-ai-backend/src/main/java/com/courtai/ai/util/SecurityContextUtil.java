package com.courtai.ai.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.util.stream.Collectors;

/**
 * Utility to extract user information from the Spring Security context.
 */
public final class SecurityContextUtil {

    private SecurityContextUtil() {
        // Utility class
    }

    /**
     * Extracts the current logged-in user's UUID/Username from the security context.
     * @return The user UUID/username, or "SYSTEM" if not authenticated.
     */
    public static String getCurrentUserUuid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return auth.getName();
        }
        return "SYSTEM";
    }

    /**
     * Extracts the current logged-in user's roles.
     * @return A comma-separated string of roles, or "ROLE_SYSTEM" if not authenticated.
     */
    public static String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getAuthorities().isEmpty()) {
            return auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
        }
        return "ROLE_SYSTEM";
    }
}
