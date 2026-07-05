package com.courtai.security;

import com.courtai.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security {@link UserDetails} adapter for the {@link User} entity.
 *
 * <p>Bridges the domain {@link User} model with Spring Security's authentication
 * framework without directly coupling the entity to security concerns.</p>
 *
 * <p>Uses constructor injection — no field injection.</p>
 */
public record UserPrincipal(User user) implements UserDetails {

    /**
     * Returns the single granted authority derived from the user's role.
     * Role name already includes the {@code ROLE_} prefix per Spring convention.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    /**
     * Returns the BCrypt-hashed password stored in the database.
     * Spring Security will use {@link org.springframework.security.crypto.password.PasswordEncoder}
     * to verify it — never compared in plaintext.
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * Returns the email as the primary login identifier (username in Spring Security terms).
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Expiry managed via isActive flag
    }

    @Override
    public boolean isAccountNonLocked() {
        return !Boolean.TRUE.equals(user.getIsLocked());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(user.getIsActive())
                && !Boolean.TRUE.equals(user.getIsDeleted());
    }

    /**
     * Convenience accessor to get the user's UUID (used in DTOs instead of Long id).
     */
    public String getUserUuid() {
        return user.getUuid();
    }

    /**
     * Convenience accessor to get the user's role name.
     */
    public String getRoleName() {
        return user.getRole().name();
    }
}
