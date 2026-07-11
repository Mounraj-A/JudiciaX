package com.courtai.auth.service;

/**
 * Service for mobile OTP generation and verification.
 */
public interface MobileOtpService {
    /** Generates a 6-digit OTP for the phone number and returns the raw OTP (simulating SMS). */
    String generateOtp(String userUuid, String phoneNumber);

    /** Verifies the OTP and marks the mobile number as verified on success. */
    void verifyOtp(String userUuid, String phoneNumber, String rawOtp);
}
