package com.courtai.auth.service.impl;

import com.courtai.audit.service.AuditService;
import com.courtai.auth.dto.request.LoginRequest;
import com.courtai.auth.dto.request.RegisterRequest;
import com.courtai.auth.dto.response.LoginResponse;
import com.courtai.auth.dto.response.RegisterResponse;
import com.courtai.auth.dto.response.UserProfileResponse;
import com.courtai.auth.entity.RefreshToken;
import com.courtai.auth.entity.UserSession;
import com.courtai.auth.repository.RefreshTokenRepository;
import com.courtai.auth.service.*;
import com.courtai.common.constants.AppConstants;
import com.courtai.common.constants.ErrorCode;
import com.courtai.common.enums.*;
import com.courtai.exception.*;
import com.courtai.notification.entity.Notification;
import com.courtai.notification.repository.NotificationRepository;
import com.courtai.security.UserPrincipal;
import com.courtai.security.jwt.JwtTokenProvider;
import com.courtai.common.validation.PasswordValidator;
import com.courtai.user.entity.User;
import com.courtai.user.repository.UserRepository;
import com.courtai.util.TokenHashUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Core authentication service implementation.
 *
 * <p>Handles registration, login (with brute-force protection), and logout.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository             userRepository;
    private final PasswordEncoder            passwordEncoder;
    private final PasswordValidator          passwordValidator;
    private final AuthenticationManager      authenticationManager;
    private final JwtTokenProvider           jwtTokenProvider;
    private final RefreshTokenRepository     refreshTokenRepository;
    private final EmailVerificationService   emailVerificationService;
    private final PasswordHistoryService     passwordHistoryService;
    private final LoginHistoryService        loginHistoryService;
    private final UserSessionService         userSessionService;
    private final SecurityEventService       securityEventService;
    private final AuditService               auditService;
    private final NotificationRepository     notificationRepository;

    @Value("${security.jwt.expiration-ms}")
    private long accessExpirationMs;

    @Value("${security.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    // =========================================================
    //  REGISTRATION
    // =========================================================

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        log.info("Registration attempt for email: [{}]", request.getEmail());

        // Confirm passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordPolicyException(List.of("Password and confirm password do not match"));
        }

        // Normalise email
        String email = request.getEmail().trim().toLowerCase();

        // Uniqueness checks
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new PhoneAlreadyExistsException(request.getPhoneNumber());
        }

        // Password policy
        List<String> violations = passwordValidator.validate(request.getPassword(), email);
        if (!violations.isEmpty()) {
            throw new PasswordPolicyException(violations);
        }

        // Build username from email local part
        String username = email.substring(0, email.indexOf('@')) + "_" + UUID.randomUUID().toString().substring(0, 6);

        // Split full name into first and last name
        String[] nameParts = request.getFullName().trim().split("\\s+", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        // Create user
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .fullName(request.getFullName())
                .email(email)
                .username(username)
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ROLE_ADVOCATE)   // Self-registration → ADVOCATE only
                .accountStatus(AccountStatus.PENDING_VERIFICATION)
                .isEmailVerified(Boolean.FALSE)
                .isMobileVerified(Boolean.FALSE)
                .isActive(Boolean.TRUE)
                .failedLoginAttempts(0)
                .profileCompletionPercent(calculateCompletion(request))
                .build();

        User savedUser = userRepository.save(user);

        // Seed initial password history
        passwordHistoryService.saveToHistory(savedUser, savedUser.getPasswordHash());

        // Generate email verification token
        String verificationToken = emailVerificationService.generateVerificationToken(savedUser.getUuid());

        // Audit
        auditService.logSuccess("USER_REGISTERED", "User", savedUser.getUuid(),
                "New advocate registered: " + email);

        log.info("User registered successfully with UUID: [{}]", savedUser.getUuid());

        return RegisterResponse.builder()
                .uuid(savedUser.getUuid())
                .email(savedUser.getEmail())
                .message("Registration successful. Please verify your email to activate your account.")
                .emailVerificationToken(verificationToken)
                .build();
    }

    // =========================================================
    //  LOGIN
    // =========================================================

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String email    = request.getEmail().trim().toLowerCase();
        String ipAddress = extractIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        DeviceInfo device = parseUserAgent(userAgent);

        log.info("Login attempt for email: [{}] from IP: [{}]", email, ipAddress);

        // Load user
        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> {
                    securityEventService.record(SecurityEventType.FAILED_LOGIN, email,
                            "Login attempt with non-existent email", ipAddress, "MEDIUM");
                    return new InvalidCredentialsException(ErrorCode.AUTH_001.getMessage());
                });

        // Check timed lock expiry — auto-unlock if expired
        if (user.isTimedLockExpired() && user.getAccountStatus() == AccountStatus.LOCKED) {
            user.setAccountStatus(AccountStatus.ACTIVE);
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(null);
            userRepository.save(user);
            securityEventService.record(SecurityEventType.ACCOUNT_AUTO_UNLOCKED, user,
                    "Account auto-unlocked after timed lock expiry", ipAddress,
                    device.device(), device.browser(), "LOW");
        }

        // Check if still locked
        if (user.isEffectivelyLocked()) {
            loginHistoryService.record(user, LoginStatus.LOCKED, ipAddress,
                    device.device(), device.browser(), device.os(), null);
            throw new AccountLockedException("Account is locked. Please try again later or contact support.");
        }

        // Check account status
        if (user.getAccountStatus() == AccountStatus.PENDING_VERIFICATION) {
            throw new AccountNotVerifiedException("Account is pending email verification or admin approval.");
        }
        if (user.getAccountStatus() == AccountStatus.SUSPENDED ||
                user.getAccountStatus() == AccountStatus.INACTIVE) {
            throw new AccountNotVerifiedException("Account is " + user.getAccountStatus().name().toLowerCase() + ". Contact support.");
        }

        // Authenticate via Spring Security
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword()));

            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

            // Reset failed attempts on success
            user.setFailedLoginAttempts(0);
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            // Generate tokens
            String accessToken  = jwtTokenProvider.generateToken(authentication);
            String rawRefresh   = UUID.randomUUID().toString();
            String refreshHash  = TokenHashUtil.hash(rawRefresh);

            // Create session
            UserSession session = userSessionService.createSession(user, accessToken, rawRefresh,
                    ipAddress, device.device(), device.browser(), device.os());

            // Save refresh token
            RefreshToken refreshToken = RefreshToken.builder()
                    .user(user)
                    .tokenHash(refreshHash)
                    .expiryDate(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000))
                    .sessionId(session.getUuid())
                    .ipAddress(ipAddress)
                    .build();
            refreshTokenRepository.save(refreshToken);

            // Record login history
            loginHistoryService.record(user, LoginStatus.SUCCESS, ipAddress,
                    device.device(), device.browser(), device.os(), session.getUuid());

            // New device alert
            boolean trusted = loginHistoryService.isTrustedDevice(user, ipAddress);
            if (!trusted) {
                securityEventService.record(SecurityEventType.NEW_DEVICE_LOGIN, user,
                        "Login from new device/IP: " + ipAddress, ipAddress,
                        device.device(), device.browser(), "MEDIUM");
                createLoginNotification(user, ipAddress, device.device());
            }

            // Audit
            auditService.logSuccess("USER_LOGIN", "User", user.getUuid(),
                    "Successful login from IP: " + ipAddress);

            // Build profile response
            UserProfileResponse profileResponse = buildProfileResponse(user, principal);

            log.info("Login successful for user: [{}]", email);

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(rawRefresh)
                    .expiresIn(accessExpirationMs)
                    .user(profileResponse)
                    .build();

        } catch (BadCredentialsException ex) {
            handleFailedLogin(user, ipAddress, device);
            throw new InvalidCredentialsException(ErrorCode.AUTH_001.getMessage());
        }
    }

    // =========================================================
    //  LOGOUT
    // =========================================================

    @Override
    @Transactional
    public void logout(String accessToken, HttpServletRequest httpRequest) {
        if (accessToken == null || accessToken.isBlank()) return;

        String rawToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        String email    = jwtTokenProvider.extractUsername(rawToken);
        String ipAddress = extractIp(httpRequest);

        userRepository.findByEmailAndIsDeletedFalse(email).ifPresent(user -> {
            userSessionService.revokeAllSessions(user);
            refreshTokenRepository.revokeAllByUser(user);
            user.setLastLogout(LocalDateTime.now());
            userRepository.save(user);
            auditService.logSuccess("USER_LOGOUT", "User", user.getUuid(),
                    "User logged out from IP: " + ipAddress);
            log.info("User [{}] logged out", email);
        });
    }

    // =========================================================
    //  PRIVATE HELPERS
    // =========================================================

    private void handleFailedLogin(User user, String ipAddress, DeviceInfo device) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= AppConstants.MAX_FAILED_ATTEMPTS) {
            user.setAccountStatus(AccountStatus.LOCKED);
            user.setAccountLockedUntil(
                    LocalDateTime.now().plusMinutes(AppConstants.ACCOUNT_LOCK_DURATION_MINUTES));
            log.warn("Account LOCKED for user [{}] after {} failed attempts", user.getEmail(), attempts);

            securityEventService.record(SecurityEventType.ACCOUNT_LOCKED, user,
                    "Account locked after " + attempts + " failed attempts",
                    ipAddress, device.device(), device.browser(), "HIGH");

            auditService.logFailure("ACCOUNT_LOCKED", "User", user.getUuid(),
                    "Account locked after " + attempts + " failed login attempts");
        }

        userRepository.save(user);
        loginHistoryService.record(user, LoginStatus.FAILED, ipAddress,
                device.device(), device.browser(), device.os(), null);
        securityEventService.record(SecurityEventType.FAILED_LOGIN, user,
                "Failed login attempt " + attempts, ipAddress, device.device(), device.browser(), "MEDIUM");
    }

    private UserProfileResponse buildProfileResponse(User user, UserPrincipal principal) {
        List<String> permissions = principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(a -> !a.startsWith("ROLE_"))
                .toList();

        return UserProfileResponse.builder()
                .uuid(user.getUuid())
                .fullName(user.getFullName())
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

    private void createLoginNotification(User user, String ipAddress, String device) {
        try {
            Notification notification = Notification.builder()
                    .recipient(user)
                    .notificationType(com.courtai.common.enums.NotificationType.IN_APP)
                    .title("New Login Detected")
                    .message("A new login was detected from " + device + " at " + ipAddress +
                             ". If this was not you, please secure your account.")
                    .isRead(false)
                    .isSent(false)
                    .build();
            notificationRepository.save(notification);
        } catch (Exception ex) {
            log.error("Failed to create login notification: {}", ex.getMessage());
        }
    }

    private String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private DeviceInfo parseUserAgent(String userAgent) {
        if (userAgent == null) return new DeviceInfo("Unknown", "Unknown", "Unknown");
        String device = userAgent.contains("Mobile") ? "Mobile" : "Desktop";
        String browser = "Unknown";
        if (userAgent.contains("Chrome"))      browser = "Chrome";
        else if (userAgent.contains("Firefox")) browser = "Firefox";
        else if (userAgent.contains("Safari"))  browser = "Safari";
        else if (userAgent.contains("Edge"))    browser = "Edge";
        String os = "Unknown";
        if (userAgent.contains("Windows"))     os = "Windows";
        else if (userAgent.contains("Mac"))    os = "macOS";
        else if (userAgent.contains("Linux"))  os = "Linux";
        else if (userAgent.contains("Android")) os = "Android";
        else if (userAgent.contains("iPhone")) os = "iOS";
        return new DeviceInfo(device, browser, os);
    }

    private int calculateCompletion(RegisterRequest request) {
        int score = 0;
        if (request.getFullName()    != null && !request.getFullName().isBlank())    score += 25;
        if (request.getEmail()       != null && !request.getEmail().isBlank())       score += 25;
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) score += 25;
        return score; // Max 75% at registration; remaining 25% from profile updates
    }

    private record DeviceInfo(String device, String browser, String os) {}
}
