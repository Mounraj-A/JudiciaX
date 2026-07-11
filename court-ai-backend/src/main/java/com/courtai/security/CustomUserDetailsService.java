package com.courtai.security;

import com.courtai.auth.repository.RoleRepository;
import com.courtai.user.entity.User;
import com.courtai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security {@link UserDetailsService} implementation.
 *
 * <p>Loads user data from the database by email address for JWT authentication.
 * Also loads the user's fine-grained permissions for method-level security.
 * Used by {@link com.courtai.config.SecurityConfig} as the authentication provider.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * Loads a user by their email address and eagerly fetches their permissions.
     *
     * @param email the email address provided during login
     * @return a populated {@link UserPrincipal} wrapping the {@link User} entity with permissions
     * @throws UsernameNotFoundException if no active user is found with the given email
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: [{}]", email);

        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: [{}]", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        // Load role + permissions for RBAC authority construction
        var roleOptional = roleRepository.findByName(user.getRole());

        log.debug("Loaded user: [{}] role: [{}] permissions: {}",
                email, user.getRole(), roleOptional.map(r -> r.getPermissions().size()).orElse(0));

        return new UserPrincipal(user, roleOptional);
    }
}
