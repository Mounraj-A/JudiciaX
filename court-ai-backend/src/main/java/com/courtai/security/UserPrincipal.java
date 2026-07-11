package com.courtai.security;

import com.courtai.auth.entity.Role;
import com.courtai.auth.repository.RoleRepository;
import com.courtai.common.enums.AccountStatus;
import com.courtai.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

/**
 * Spring Security {@link UserDetails} adapter for the {@link User} entity.
 *
 * <p>Bridges the domain {@link User} model with Spring Security's authentication
 * framework without directly coupling the entity to security concerns.</p>
 *
 * <p>Grants both coarse-grained ROLE_ authority and fine-grained permission authorities
 * for method-level security with {@code @PreAuthorize}.</p>
 */
public class UserPrincipal implements UserDetails {

    private final User user;
    private final List<GrantedAuthority> authorities;

    /**
     * Primary constructor — used when loading user WITHOUT permissions (lazy, fast path).
     */
    public UserPrincipal(User user) {
        this.user = user;
        this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    /**
     * Full constructor — used when loading user WITH permissions (RBAC eager path).
     */
    public UserPrincipal(User user, Optional<Role> roleWithPermissions) {
        this.user = user;
        List<GrantedAuthority> auths = new ArrayList<>();
        auths.add(new SimpleGrantedAuthority(user.getRole().name()));

        roleWithPermissions.ifPresent(role ->
                role.getPermissions().forEach(p ->
                        auths.add(new SimpleGrantedAuthority(p.getCode().name()))));

        this.authorities = List.copyOf(auths);
    }

    /**
     * Returns coarse ROLE_ authority plus all fine-grained permission authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * Email is the primary login identifier.
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Account is non-locked when:
     * <ul>
     *   <li>Legacy isLocked = false</li>
     *   <li>accountStatus != LOCKED, or timed lock has expired</li>
     * </ul>
     */
    @Override
    public boolean isAccountNonLocked() {
        if (Boolean.TRUE.equals(user.getIsLocked()) &&
                user.getAccountStatus() != AccountStatus.ACTIVE) {
            return false;
        }
        if (user.getAccountStatus() == AccountStatus.LOCKED) {
            return user.isTimedLockExpired(); // Auto-unlock if expired
        }
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Account is enabled only when status is ACTIVE (or legacy isActive=true for old records).
     */
    @Override
    public boolean isEnabled() {
        AccountStatus status = user.getAccountStatus();
        if (status != null) {
            return status == AccountStatus.ACTIVE && !Boolean.TRUE.equals(user.getIsDeleted());
        }
        // Fallback for legacy records without accountStatus
        return Boolean.TRUE.equals(user.getIsActive()) && !Boolean.TRUE.equals(user.getIsDeleted());
    }

    /** The user's UUID (never expose internal Long id). */
    public String getUserUuid() {
        return user.getUuid();
    }

    /** The user's role name (e.g., "ROLE_ADMIN"). */
    public String getRoleName() {
        return user.getRole().name();
    }

    /** The underlying User entity. */
    public User user() {
        return user;
    }
}
