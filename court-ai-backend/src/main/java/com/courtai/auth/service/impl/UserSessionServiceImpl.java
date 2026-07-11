package com.courtai.auth.service.impl;

import com.courtai.auth.dto.response.SessionResponse;
import com.courtai.auth.entity.UserSession;
import com.courtai.auth.repository.UserSessionRepository;
import com.courtai.auth.service.UserSessionService;
import com.courtai.common.enums.SessionStatus;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.user.entity.User;
import com.courtai.util.TokenHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository userSessionRepository;

    @Override
    @Transactional
    public UserSession createSession(User user, String accessToken, String refreshToken,
                                     String ipAddress, String device, String browser, String os) {
        UserSession session = UserSession.builder()
                .user(user)
                .accessTokenHash(TokenHashUtil.hash(accessToken))
                .refreshTokenHash(TokenHashUtil.hash(refreshToken))
                .ipAddress(ipAddress)
                .device(device)
                .browser(browser)
                .operatingSystem(os)
                .loginTime(LocalDateTime.now())
                .lastActivityAt(LocalDateTime.now())
                .status(SessionStatus.ACTIVE)
                .isTrustedDevice(false)
                .build();
        return userSessionRepository.save(session);
    }

    @Override
    public List<SessionResponse> getSessionsForUser(User user, String currentAccessToken) {
        String currentHash = TokenHashUtil.hash(currentAccessToken);
        return userSessionRepository.findByUserAndStatusOrderByLoginTimeDesc(user, SessionStatus.ACTIVE)
                .stream()
                .map(s -> SessionResponse.builder()
                        .uuid(s.getUuid())
                        .device(s.getDevice())
                        .browser(s.getBrowser())
                        .operatingSystem(s.getOperatingSystem())
                        .ipAddress(s.getIpAddress())
                        .isTrustedDevice(s.getIsTrustedDevice())
                        .status(s.getStatus())
                        .loginTime(s.getLoginTime())
                        .lastActivityAt(s.getLastActivityAt())
                        .isCurrent(s.getAccessTokenHash() != null && s.getAccessTokenHash().equals(currentHash))
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void revokeSession(User user, String sessionUuid) {
        UserSession session = userSessionRepository.findByUuidAndUser(sessionUuid, user)
                .orElseThrow(() -> new ResourceNotFoundException("Session", "uuid", sessionUuid));
        userSessionRepository.updateStatusByUuid(sessionUuid, SessionStatus.REVOKED);
        log.info("Session [{}] revoked for user [{}]", sessionUuid, user.getEmail());
    }

    @Override
    @Transactional
    public void revokeAllSessions(User user) {
        userSessionRepository.revokeAllActiveSessionsForUser(user);
        log.info("All sessions revoked for user [{}]", user.getEmail());
    }

    @Override
    @Transactional
    public void updateTokenHashes(String oldAccessTokenHash, String newAccessToken, String newRefreshToken) {
        userSessionRepository.findByAccessTokenHash(oldAccessTokenHash).ifPresent(session -> {
            session.setAccessTokenHash(TokenHashUtil.hash(newAccessToken));
            session.setRefreshTokenHash(TokenHashUtil.hash(newRefreshToken));
            session.setLastActivityAt(LocalDateTime.now());
            userSessionRepository.save(session);
        });
    }
}
