package com.courtai.admin.service.impl;

import com.courtai.admin.service.AdminUserService;
import com.courtai.audit.service.AuditService;
import com.courtai.auth.dto.response.UserProfileResponse;
import com.courtai.auth.service.PasswordResetService;
import com.courtai.auth.service.SecurityEventService;
import com.courtai.auth.service.UserSessionService;
import com.courtai.common.enums.AccountStatus;
import com.courtai.common.enums.SecurityEventType;
import com.courtai.common.enums.UserRole;
import com.courtai.exception.BusinessRuleViolationException;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.notification.entity.Notification;
import com.courtai.notification.repository.NotificationRepository;
import com.courtai.user.dto.UserResponse;
import com.courtai.user.entity.User;
import com.courtai.user.mapper.UserMapper;
import com.courtai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository       userRepository;
    private final UserMapper           userMapper;
    private final AuditService         auditService;
    private final SecurityEventService securityEventService;
    private final NotificationRepository notificationRepository;
    private final PasswordResetService passwordResetService;
    private final UserSessionService   userSessionService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    @Override
    public UserProfileResponse getUserByUuid(String uuid) {
        User user = loadUser(uuid);
        return UserProfileResponse.builder()
                .uuid(user.getUuid())
                .fullName(user.getDisplayName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .accountStatus(user.getAccountStatus())
                .emailVerified(user.getIsEmailVerified())
                .mobileVerified(user.getIsMobileVerified())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .profileCompletionPercent(user.getProfileCompletionPercent())
                .permissions(List.of())
                .build();
    }

    @Override
    @Transactional
    public void approveUser(String uuid, String adminUuid) {
        User user = loadUser(uuid);

        if (!Boolean.TRUE.equals(user.getIsEmailVerified())) {
            throw new BusinessRuleViolationException(
                    "Cannot approve user with unverified email. User must verify email first.");
        }

        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setIsActive(Boolean.TRUE);
        userRepository.save(user);

        notify(user, "Account Approved",
                "Your account has been verified and approved. You can now log in to JudiciaX.");

        auditService.logSuccess("USER_APPROVED", "User", uuid,
                "User approved by admin: " + adminUuid);
        log.info("User [{}] approved by admin [{}]", uuid, adminUuid);
    }

    @Override
    @Transactional
    public void rejectUser(String uuid, String reason, String adminUuid) {
        User user = loadUser(uuid);
        user.setAccountStatus(AccountStatus.SUSPENDED);
        userRepository.save(user);

        notify(user, "Account Rejected",
                "Your account verification was unsuccessful. Reason: " + reason +
                ". Please contact support for assistance.");

        auditService.logFailure("USER_REJECTED", "User", uuid,
                "User rejected by admin [" + adminUuid + "]: " + reason);
        log.info("User [{}] rejected by admin [{}] for: {}", uuid, adminUuid, reason);
    }

    @Override
    @Transactional
    public void lockUser(String uuid, String reason, String adminUuid) {
        User user = loadUser(uuid);
        user.setAccountStatus(AccountStatus.LOCKED);
        user.setIsLocked(Boolean.TRUE);
        userRepository.save(user);

        userSessionService.revokeAllSessions(user);
        securityEventService.record(SecurityEventType.ACCOUNT_LOCKED, user,
                "Account manually locked by admin: " + reason, null, null, null, "HIGH");

        notify(user, "Account Locked",
                "Your account has been temporarily locked. Reason: " + reason +
                ". Contact support for assistance.");

        auditService.logSuccess("USER_LOCKED", "User", uuid,
                "Locked by admin [" + adminUuid + "]: " + reason);
        log.info("User [{}] locked by admin [{}]", uuid, adminUuid);
    }

    @Override
    @Transactional
    public void unlockUser(String uuid, String adminUuid) {
        User user = loadUser(uuid);
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setIsLocked(Boolean.FALSE);
        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);
        userRepository.save(user);

        securityEventService.record(SecurityEventType.ACCOUNT_AUTO_UNLOCKED, user,
                "Account manually unlocked by admin", null, null, null, "LOW");

        notify(user, "Account Unlocked",
                "Your account has been unlocked. You can now log in to JudiciaX.");

        auditService.logSuccess("USER_UNLOCKED", "User", uuid,
                "Unlocked by admin: " + adminUuid);
        log.info("User [{}] unlocked by admin [{}]", uuid, adminUuid);
    }

    @Override
    @Transactional
    public String adminResetPassword(String uuid, String adminUuid) {
        User user = loadUser(uuid);
        String token = passwordResetService.generateResetToken(user.getEmail(), "ADMIN_ACTION");

        auditService.logSuccess("ADMIN_PASSWORD_RESET", "User", uuid,
                "Password reset initiated by admin: " + adminUuid);
        log.info("Password reset token generated for user [{}] by admin [{}]", uuid, adminUuid);
        return token;
    }

    @Override
    @Transactional
    public void assignRole(String uuid, UserRole newRole, String adminUuid) {
        User user = loadUser(uuid);
        UserRole oldRole = user.getRole();
        user.setRole(newRole);
        userRepository.save(user);

        auditService.logSuccess("ROLE_CHANGED", "User", uuid,
                "Role changed from " + oldRole + " to " + newRole + " by admin: " + adminUuid);
        log.info("Role for user [{}] changed from [{}] to [{}] by admin [{}]",
                uuid, oldRole, newRole, adminUuid);
    }

    @Override
    @Transactional
    public void deleteUser(String uuid, String adminUuid) {
        User user = loadUser(uuid);
        user.setAccountStatus(AccountStatus.SOFT_DELETED);
        user.softDelete();
        userRepository.save(user);

        auditService.logSuccess("USER_DELETED", "User", uuid,
                "Soft-deleted by admin: " + adminUuid);
        log.info("User [{}] soft-deleted by admin [{}]", uuid, adminUuid);
    }

    @Override
    @Transactional
    public UserResponse createUser(String email, String fullName, String phone, String password, UserRole role, String adminUuid) {
        if (userRepository.existsByEmail(email)) {
            throw new com.courtai.exception.EmailAlreadyExistsException("User with email " + email + " already exists");
        }
        if (phone != null && !phone.trim().isEmpty() && userRepository.existsByPhoneNumber(phone.trim())) {
            throw new com.courtai.exception.PhoneAlreadyExistsException("User with phone number " + phone + " already exists");
        }
        if (fullName != null && !fullName.trim().isEmpty() && userRepository.existsByFullName(fullName.trim())) {
            throw new com.courtai.exception.DuplicateResourceException("User with name " + fullName + " already exists");
        }
        
        String baseUsername = email != null && email.contains("@") ? email.split("@")[0].replaceAll("[^a-zA-Z0-9]", "") : "user";
        if (baseUsername.isEmpty()) baseUsername = "user";
        
        String username = baseUsername;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter++;
        }
        
        String firstName = username;
        String lastName = "";
        if (fullName != null && !fullName.trim().isEmpty()) {
            String[] parts = fullName.trim().split(" ", 2);
            firstName = parts[0];
            lastName = parts.length > 1 ? parts[1] : "";
        }

        String validPhone = phone != null && !phone.trim().isEmpty() ? phone.trim() : null;

        User user = User.builder()
                .email(email)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(validPhone)
                .fullName(fullName)
                .passwordHash(password != null && !password.isEmpty() ? passwordEncoder.encode(password) : passwordEncoder.encode("Password@123"))
                .role(role != null ? role : UserRole.ROLE_ADVOCATE)
                .accountStatus(AccountStatus.ACTIVE)
                .isActive(true)
                .isEmailVerified(true)
                .isMobileVerified(false)
                .failedLoginAttempts(0)
                .build();
                
        user = userRepository.save(user);

        auditService.logSuccess("USER_CREATED", "User", user.getUuid(),
                "User created by admin: " + adminUuid);
        log.info("User [{}] created by admin [{}]", user.getUuid(), adminUuid);
        
        return userMapper.toResponse(user);
    }

    // =========================================================
    //  PRIVATE
    // =========================================================

    private User loadUser(String uuid) {
        return userRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", uuid));
    }

    private void notify(User user, String title, String message) {
        try {
            Notification n = Notification.builder()
                    .recipient(user)
                    .notificationType(com.courtai.common.enums.NotificationType.IN_APP)
                    .title(title)
                    .message(message)
                    .isRead(false)
                    .isSent(false)
                    .build();
            notificationRepository.save(n);
        } catch (Exception ex) {
            log.error("Failed to create notification for user [{}]: {}", user.getEmail(), ex.getMessage());
        }
    }
}
