package com.courtai.security;

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
 * Used by {@link com.courtai.config.SecurityConfig} as the authentication provider.</p>
 *
 * <p>Uses constructor injection via Lombok {@code @RequiredArgsConstructor}.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by their email address (used as the primary authentication identifier).
     *
     * @param email the email address provided during login
     * @return a populated {@link UserPrincipal} wrapping the {@link User} entity
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

        log.debug("Successfully loaded user: [{}] with role: [{}]", email, user.getRole());
        return new UserPrincipal(user);
    }
}
