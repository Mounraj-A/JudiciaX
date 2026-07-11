package com.courtai.user.service;

import com.courtai.audit.service.AuditService;
import com.courtai.auth.dto.request.ChangePasswordRequest;
import com.courtai.auth.dto.response.SessionResponse;
import com.courtai.auth.dto.response.UserProfileResponse;
import com.courtai.auth.service.PasswordHistoryService;
import com.courtai.auth.service.UserSessionService;
import com.courtai.common.enums.AccountStatus;
import com.courtai.common.validation.PasswordValidator;
import com.courtai.exception.*;
import com.courtai.security.UserPrincipal;
import com.courtai.user.dto.*;
import com.courtai.user.entity.User;
import com.courtai.user.entity.UserPrivacySettings;
import com.courtai.user.mapper.UserMapper;
import com.courtai.user.repository.UserPrivacySettingsRepository;
import com.courtai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link UserService}.
 *
 * <p>Handles both admin user management and self-service profile operations.
 * No business logic in controllers — all logic lives here.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository            userRepository;
    private final UserMapper                userMapper;
    private final PasswordEncoder           passwordEncoder;
    private final PasswordValidator         passwordValidator;
    private final PasswordHistoryService    passwordHistoryService;
    private final UserSessionService        userSessionService;
    private final AuditService              auditService;
    private final UserPrivacySettingsRepository privacySettingsRepository;

    // =========================================================
    //  ADMIN METHODS (existing)
    // =========================================================

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with email: [{}] and role: [{}]", request.getEmail(), request.getRole());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .fullName(request.getFirstName() + " " + request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .isActive(true)
                .isEmailVerified(false)
                .isLocked(false)
                .accountStatus(AccountStatus.PENDING_VERIFICATION)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully with UUID: [{}]", savedUser.getUuid());
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserByUuid(String uuid) {
        log.debug("Fetching user by UUID: [{}]", uuid);
        User user = userRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", uuid));
        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all active users");
        return userMapper.toResponseList(userRepository.findAllByIsDeletedFalse());
    }

    @Override
    @Transactional
    public void deleteUser(String uuid) {
        log.info("Soft-deleting user with UUID: [{}]", uuid);
        User user = userRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", uuid));
        user.setAccountStatus(AccountStatus.SOFT_DELETED);
        user.softDelete();
        userRepository.save(user);
        auditService.logSuccess("USER_DELETED", "User", uuid, "User soft-deleted");
        log.info("User soft-deleted: [{}]", uuid);
    }

    // =========================================================
    //  SELF-SERVICE PROFILE METHODS (new)
    // =========================================================

    @Override
    public UserProfileResponse getMyProfile(UserPrincipal principal) {
        User user = loadUser(principal.getUserUuid());
        List<String> permissions = principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(a -> !a.startsWith("ROLE_"))
                .toList();

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
                .permissions(permissions)
                .build();
    }

    @Override
    @Transactional
    public UserProfileResponse updateMyProfile(UserPrincipal principal, UpdateProfileRequest request) {
        User user = loadUser(principal.getUserUuid());

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber()) &&
                    !request.getPhoneNumber().equals(user.getPhoneNumber())) {
                throw new PhoneAlreadyExistsException(request.getPhoneNumber());
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }
        user.setProfileCompletionPercent(calculateCompletion(user));
        User saved = userRepository.save(user);

        auditService.logSuccess("PROFILE_UPDATED", "User", user.getUuid(), "Profile updated");
        log.info("Profile updated for user [{}]", user.getEmail());
        return getMyProfile(principal);
    }

    @Override
    @Transactional
    public void changePassword(UserPrincipal principal, ChangePasswordRequest request) {
        User user = loadUser(principal.getUserUuid());

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordPolicyException(List.of("New password and confirm password do not match"));
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new PasswordPolicyException(List.of("New password cannot be the same as the current password"));
        }

        List<String> violations = passwordValidator.validate(request.getNewPassword(), user.getEmail());
        if (!violations.isEmpty()) {
            throw new PasswordPolicyException(violations);
        }

        if (passwordHistoryService.isPasswordReused(user, request.getNewPassword())) {
            throw new PasswordPolicyException(List.of("Password was recently used. Please choose a different one."));
        }

        passwordHistoryService.saveToHistory(user, user.getPasswordHash());
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        auditService.logSuccess("PASSWORD_CHANGED", "User", user.getUuid(), "Password changed by user");
        log.info("Password changed for user [{}]", user.getEmail());
    }

    @Override
    public List<SessionResponse> getMySessions(UserPrincipal principal, String currentAccessToken) {
        User user = loadUser(principal.getUserUuid());
        return userSessionService.getSessionsForUser(user, currentAccessToken);
    }

    @Override
    @Transactional
    public void revokeSession(UserPrincipal principal, String sessionUuid) {
        User user = loadUser(principal.getUserUuid());
        userSessionService.revokeSession(user, sessionUuid);
        auditService.logSuccess("SESSION_REVOKED", "User", user.getUuid(),
                "Session revoked: " + sessionUuid);
    }

    @Override
    @Transactional
    public void revokeAllSessions(UserPrincipal principal) {
        User user = loadUser(principal.getUserUuid());
        userSessionService.revokeAllSessions(user);
        auditService.logSuccess("ALL_SESSIONS_REVOKED", "User", user.getUuid(),
                "All sessions revoked");
    }

    @Override
    @Transactional
    public void updatePrivacySettings(UserPrincipal principal, PrivacySettingsRequest request) {
        User user = loadUser(principal.getUserUuid());
        UserPrivacySettings settings = privacySettingsRepository.findByUser(user)
                .orElse(UserPrivacySettings.builder().user(user).build());

        if (request.getEmailNotifications() != null) settings.setEmailNotifications(request.getEmailNotifications());
        if (request.getSmsNotifications()   != null) settings.setSmsNotifications(request.getSmsNotifications());
        if (request.getDarkMode()           != null) settings.setDarkMode(request.getDarkMode());
        if (request.getLanguage()           != null && !request.getLanguage().isBlank())
            settings.setLanguage(request.getLanguage());

        privacySettingsRepository.save(settings);
        auditService.logSuccess("PRIVACY_SETTINGS_UPDATED", "User", user.getUuid(), "Privacy settings updated");
        log.info("Privacy settings updated for user [{}]", user.getEmail());
    }

    // =========================================================
    //  PRIVATE HELPERS
    // =========================================================

    private User loadUser(String uuid) {
        return userRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", uuid));
    }

    private int calculateCompletion(User user) {
        int score = 0;
        if (user.getFullName()    != null && !user.getFullName().isBlank())    score += 20;
        if (user.getEmail()       != null && !user.getEmail().isBlank())       score += 20;
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()) score += 20;
        if (Boolean.TRUE.equals(user.getIsEmailVerified()))                    score += 20;
        if (Boolean.TRUE.equals(user.getIsMobileVerified()))                   score += 20;
        return score;
    }
}
