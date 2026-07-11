package com.courtai.auth.service.impl;

import com.courtai.auth.dto.response.RefreshTokenResponse;
import com.courtai.auth.entity.RefreshToken;
import com.courtai.auth.repository.RefreshTokenRepository;
import com.courtai.auth.service.RefreshTokenService;
import com.courtai.auth.service.UserSessionService;
import com.courtai.common.enums.SecurityEventType;
import com.courtai.exception.InvalidTokenException;
import com.courtai.exception.TokenExpiredException;
import com.courtai.security.UserPrincipal;
import com.courtai.auth.service.SecurityEventService;
import com.courtai.security.jwt.JwtTokenProvider;
import com.courtai.util.TokenHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final UserSessionService userSessionService;
    private final SecurityEventService securityEventService;

    @Value("${security.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Value("${security.jwt.expiration-ms}")
    private long accessExpirationMs;

    @Override
    @Transactional
    public RefreshTokenResponse rotate(String rawRefreshToken) {
        String hash = TokenHashUtil.hash(rawRefreshToken);

        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found or already used"));

        if (Boolean.TRUE.equals(stored.getUsed())) {
            throw new InvalidTokenException("Refresh token has already been used");
        }

        if (!stored.isValid()) {
            throw new TokenExpiredException("Refresh token has expired");
        }

        // Mark old token as used (rotation)
        stored.setUsed(Boolean.TRUE);
        refreshTokenRepository.save(stored);

        // Load user and generate new token pair
        String email = stored.getUser().getEmail();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String newAccessToken  = jwtTokenProvider.generateToken(userDetails);
        String newRawRefresh   = UUID.randomUUID().toString();
        String newRefreshHash  = TokenHashUtil.hash(newRawRefresh);

        // Save new refresh token
        RefreshToken newToken = RefreshToken.builder()
                .user(stored.getUser())
                .tokenHash(newRefreshHash)
                .expiryDate(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000))
                .sessionId(stored.getSessionId())
                .build();
        refreshTokenRepository.save(newToken);

        // Update session token hashes
        userSessionService.updateTokenHashes(TokenHashUtil.hash(rawRefreshToken), newAccessToken, newRawRefresh);

        // Log security event
        securityEventService.record(SecurityEventType.TOKEN_REFRESH, stored.getUser(),
                "Refresh token rotated", stored.getIpAddress(), null, null, "LOW");

        log.info("Token rotated for user: [{}]", email);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRawRefresh)
                .expiresIn(accessExpirationMs)
                .build();
    }

    @Override
    @Transactional
    public void revokeAllForUser(Long userId) {
        refreshTokenRepository.findAll().stream()
                .filter(rt -> rt.getUser().getId().equals(userId))
                .forEach(rt -> { rt.setUsed(true); refreshTokenRepository.save(rt); });
    }

    @Override
    @Transactional
    public void revokeForSession(String sessionId) {
        refreshTokenRepository.revokeBySessionId(sessionId);
    }
}
