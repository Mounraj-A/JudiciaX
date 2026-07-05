package com.courtai.auth.service;

import com.courtai.auth.dto.AuthResponse;
import com.courtai.auth.dto.LoginRequest;
import com.courtai.security.UserPrincipal;
import com.courtai.security.jwt.JwtTokenProvider;
import com.courtai.user.entity.User;
import com.courtai.user.repository.UserRepository;
import com.courtai.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link AuthService}.
 *
 * <p>Uses Spring Security's {@link AuthenticationManager} for credential verification,
 * then issues JWT tokens via {@link JwtTokenProvider}.</p>
 *
 * <p>NOTE: Token blacklisting for logout and refresh token storage
 * will be implemented in a future phase using Redis or a DB token store.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${security.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: [{}]", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = principal.user();

        String accessToken  = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        log.info("Login successful for user: [{}] with role: [{}]", user.getEmail(), user.getRole());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs / 1000)
                .userUuid(user.getUuid())
                .email(user.getEmail())
                .role(user.getRole().name())
                .fullName(user.getFullName())
                .build();
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        log.debug("Token refresh requested");

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new com.courtai.exception.BusinessRuleViolationException("Invalid or expired refresh token");
        }

        String email = jwtTokenProvider.extractUsername(refreshToken);
        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        org.springframework.security.core.userdetails.UserDetails userDetails =
                new UserPrincipal(user);

        String newAccessToken  = jwtTokenProvider.generateToken(userDetails);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

        log.info("Token refreshed successfully for: [{}]", email);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs / 1000)
                .userUuid(user.getUuid())
                .email(user.getEmail())
                .role(user.getRole().name())
                .fullName(user.getFullName())
                .build();
    }

    @Override
    public void logout(String accessToken) {
        // TODO: Implement token blacklisting in Phase 2 (Redis-backed token store)
        log.info("Logout requested — token invalidation will be implemented in Phase 2");
    }
}
