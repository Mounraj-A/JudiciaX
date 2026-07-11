package com.courtai.auth.service.impl;

import com.courtai.auth.entity.PasswordResetToken;
import com.courtai.auth.repository.PasswordResetRepository;
import com.courtai.auth.service.PasswordHistoryService;
import com.courtai.auth.service.PasswordResetService;
import com.courtai.auth.service.SecurityEventService;
import com.courtai.common.constants.AppConstants;
import com.courtai.common.enums.SecurityEventType;
import com.courtai.common.validation.PasswordValidator;
import com.courtai.exception.*;
import com.courtai.user.entity.User;
import com.courtai.user.repository.UserRepository;
import com.courtai.util.TokenHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;
    private final PasswordHistoryService passwordHistoryService;
    private final SecurityEventService securityEventService;

    @Override
    @Transactional
    public String generateResetToken(String email, String ipAddress) {
        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        // Invalidate existing tokens
        passwordResetRepository.invalidateAllForUser(user.getId());

        String rawToken  = UUID.randomUUID().toString();
        String tokenHash = TokenHashUtil.hash(rawToken);

        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiryDate(LocalDateTime.now().plusHours(AppConstants.PASSWORD_RESET_TOKEN_EXPIRY_HOURS))
                .ipAddress(ipAddress)
                .build();

        passwordResetRepository.save(token);
        securityEventService.record(SecurityEventType.PASSWORD_RESET_REQUESTED, user,
                "Password reset requested", ipAddress, null, null, "LOW");

        log.info("Password reset token generated for user: [{}]", user.getEmail());
        return rawToken;
    }

    @Override
    @Transactional
    public void resetPassword(String rawToken, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordPolicyException("New password and confirm password do not match");
        }

        String hash = TokenHashUtil.hash(rawToken);
        PasswordResetToken token = passwordResetRepository.findByTokenHash(hash)
                .orElseThrow(() -> new InvalidTokenException("Password reset token is invalid"));

        if (Boolean.TRUE.equals(token.getUsed())) {
            throw new InvalidTokenException("Password reset token has already been used");
        }

        if (LocalDateTime.now().isAfter(token.getExpiryDate())) {
            throw new TokenExpiredException("Password reset token has expired");
        }

        User user = token.getUser();

        // Validate password policy
        List<String> violations = passwordValidator.validate(newPassword, user.getEmail());
        if (!violations.isEmpty()) {
            throw new PasswordPolicyException(violations);
        }

        // Check history
        if (passwordHistoryService.isPasswordReused(user, newPassword)) {
            throw new PasswordPolicyException(List.of("Password was recently used. Choose a different password."));
        }

        // Check not same as current
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new PasswordPolicyException(List.of("New password cannot be the same as the current password"));
        }

        // Save to history then update
        passwordHistoryService.saveToHistory(user, user.getPasswordHash());
        user.setPasswordHash(passwordEncoder.encode(newPassword));

        token.setUsed(Boolean.TRUE);
        passwordResetRepository.save(token);
        userRepository.save(user);

        securityEventService.record(SecurityEventType.PASSWORD_RESET_COMPLETED, user,
                "Password reset completed", token.getIpAddress(), null, null, "LOW");

        log.info("Password reset successful for user: [{}]", user.getEmail());
    }
}
