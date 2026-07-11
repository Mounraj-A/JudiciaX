package com.courtai.auth.service;

import com.courtai.auth.dto.request.LoginRequest;
import com.courtai.auth.dto.request.RegisterRequest;
import com.courtai.auth.dto.response.LoginResponse;
import com.courtai.auth.dto.response.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Core authentication service — registration, login, logout.
 */
public interface AuthenticationService {

    /**
     * Registers a new Advocate user account.
     *
     * @return registration response with UUID and email verification token
     */
    RegisterResponse register(RegisterRequest request, HttpServletRequest httpRequest);

    /**
     * Authenticates a user by email/password.
     *
     * @return JWT access + refresh token pair with embedded user profile
     */
    LoginResponse login(LoginRequest request, HttpServletRequest httpRequest);

    /**
     * Terminates the current session and revokes tokens.
     *
     * @param accessToken the raw access token from the Authorization header
     */
    void logout(String accessToken, HttpServletRequest httpRequest);
}
