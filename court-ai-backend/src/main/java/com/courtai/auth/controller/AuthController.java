package com.courtai.auth.controller;

import com.courtai.auth.dto.request.*;
import com.courtai.auth.dto.response.LoginResponse;
import com.courtai.auth.dto.response.RefreshTokenResponse;
import com.courtai.auth.dto.response.RegisterResponse;
import com.courtai.auth.service.AuthenticationService;
import com.courtai.auth.service.EmailVerificationService;
import com.courtai.auth.service.MobileOtpService;
import com.courtai.auth.service.PasswordResetService;
import com.courtai.auth.service.RefreshTokenService;
import com.courtai.auth.service.RateLimitService;
import com.courtai.common.constants.ApiConstants;
import com.courtai.common.dto.ApiResponse;
import com.courtai.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for all authentication endpoints.
 *
 * <p>No business logic lives here — all work is delegated to services.</p>
 */
@Slf4j
@RestController
@RequestMapping(ApiConstants.API_AUTH_BASE)
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registration, login, token management, verification")
public class AuthController {

    private final AuthenticationService    authenticationService;
    private final RefreshTokenService      refreshTokenService;
    private final PasswordResetService     passwordResetService;
    private final EmailVerificationService emailVerificationService;
    private final MobileOtpService         mobileOtpService;
    private final RateLimitService         rateLimitService;

    @PostMapping("/register")
    @Operation(summary = "Register a new Advocate account")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {
        rateLimitService.checkRateLimit(httpRequest, "/register");
        RegisterResponse response = authenticationService.register(request, httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ApiConstants.MSG_REGISTER_SUCCESS, response));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and receive JWT token pair")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        rateLimitService.checkRateLimit(httpRequest, "/login");
        LoginResponse response = authenticationService.login(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_LOGIN_SUCCESS, response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Invalidate current session and revoke all tokens")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest httpRequest) {
        authenticationService.logout(authHeader, httpRequest);
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_LOGOUT_SUCCESS));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rotate refresh token and receive new token pair")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = refreshTokenService.rotate(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_TOKEN_REFRESHED, response));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Generate a password reset token")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest) {
        rateLimitService.checkRateLimit(httpRequest, "/forgot-password");
        String resetToken = passwordResetService.generateResetToken(request.getEmail(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_FORGOT_PASSWORD_SENT, resetToken));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using the reset token")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword(), request.getConfirmPassword());
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_RESET_PASSWORD_SUCCESS));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email address using the verification token")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request) {
        emailVerificationService.verifyEmail(request.getToken());
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_EMAIL_VERIFIED));
    }

    @PostMapping("/resend-verification-email")
    @Operation(summary = "Resend the email verification link")
    public ResponseEntity<ApiResponse<Void>> resendVerificationEmail(
            @Valid @RequestBody ResendVerificationEmailRequest request) {
        // Future: look up user by email and resend
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_RESEND_VERIFICATION_EMAIL));
    }

    @PostMapping("/send-otp")
    @Operation(summary = "Send OTP to the registered mobile number")
    public ResponseEntity<ApiResponse<String>> sendOtp(
            @Valid @RequestBody ResendOtpRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest httpRequest) {
        rateLimitService.checkRateLimit(httpRequest, "/send-otp");
        String rawOtp = mobileOtpService.generateOtp(principal.getUserUuid(), request.getPhoneNumber());
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_OTP_SENT, rawOtp));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify mobile OTP")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(
            @Valid @RequestBody VerifyMobileOtpRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        mobileOtpService.verifyOtp(principal.getUserUuid(), request.getPhoneNumber(), request.getOtp());
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_OTP_VERIFIED));
    }
}
