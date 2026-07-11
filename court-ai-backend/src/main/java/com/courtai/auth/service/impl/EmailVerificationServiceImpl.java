package com.courtai.auth.service.impl;

import com.courtai.auth.entity.EmailVerificationToken;
import com.courtai.auth.repository.EmailVerificationRepository;
import com.courtai.auth.service.EmailVerificationService;
import com.courtai.common.constants.AppConstants;
import com.courtai.common.enums.AccountStatus;
import com.courtai.common.enums.UserRole;
import com.courtai.exception.InvalidTokenException;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.TokenExpiredException;
import com.courtai.user.entity.User;
import com.courtai.user.repository.UserRepository;
import com.courtai.util.TokenHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public String generateVerificationToken(String userUuid) {
        User user = userRepository.findByUuidAndIsDeletedFalse(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userUuid));

        // Invalidate any existing tokens
        emailVerificationRepository.invalidateAllForUser(user);

        String rawToken = UUID.randomUUID().toString();
        String tokenHash = TokenHashUtil.hash(rawToken);

        EmailVerificationToken token = EmailVerificationToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiryDate(LocalDateTime.now().plusHours(AppConstants.EMAIL_VERIFICATION_TOKEN_EXPIRY_HOURS))
                .build();

        emailVerificationRepository.save(token);
        log.info("Email verification token generated for user: [{}]", user.getEmail());

        return rawToken; // Caller simulates email delivery
    }

    @Override
    @Transactional
    public void verifyEmail(String rawToken) {
        String hash = TokenHashUtil.hash(rawToken);

        EmailVerificationToken token = emailVerificationRepository.findByTokenHash(hash)
                .orElseThrow(() -> new InvalidTokenException("Email verification token is invalid or not found"));

        if (Boolean.TRUE.equals(token.getUsed())) {
            throw new InvalidTokenException("Email verification token has already been used");
        }

        if (LocalDateTime.now().isAfter(token.getExpiryDate())) {
            throw new TokenExpiredException("Email verification token has expired");
        }

        // Mark token used
        token.setUsed(Boolean.TRUE);
        emailVerificationRepository.save(token);

        // Update user
        User user = token.getUser();
        user.setIsEmailVerified(Boolean.TRUE);

        // Activate only if role doesn't require admin approval
        // ROLE_ADVOCATE and ROLE_CLERK remain PENDING_VERIFICATION until admin approves
        if (user.getRole() == UserRole.ROLE_ADMIN || user.getRole() == UserRole.ROLE_JUDGE) {
            user.setAccountStatus(AccountStatus.ACTIVE);
            user.setIsActive(Boolean.TRUE);
            log.info("Account activated after email verification for user: [{}]", user.getEmail());
        } else {
            log.info("Email verified for [{}] — awaiting admin approval", user.getEmail());
        }

        userRepository.save(user);
    }
}
