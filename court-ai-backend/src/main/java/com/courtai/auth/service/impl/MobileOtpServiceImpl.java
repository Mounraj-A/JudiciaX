package com.courtai.auth.service.impl;

import com.courtai.auth.entity.MobileOtp;
import com.courtai.auth.repository.MobileOtpRepository;
import com.courtai.auth.service.MobileOtpService;
import com.courtai.common.constants.AppConstants;
import com.courtai.exception.InvalidOtpException;
import com.courtai.exception.OtpExpiredException;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.user.entity.User;
import com.courtai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MobileOtpServiceImpl implements MobileOtpService {

    private final MobileOtpRepository mobileOtpRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public String generateOtp(String userUuid, String phoneNumber) {
        User user = userRepository.findByUuidAndIsDeletedFalse(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userUuid));

        // Invalidate old OTPs
        mobileOtpRepository.invalidateAllForUser(user);

        // Generate 6-digit OTP
        String rawOtp = String.format("%0" + AppConstants.OTP_LENGTH + "d",
                RANDOM.nextInt((int) Math.pow(10, AppConstants.OTP_LENGTH)));

        MobileOtp otp = MobileOtp.builder()
                .user(user)
                .otpHash(passwordEncoder.encode(rawOtp))
                .phoneNumber(phoneNumber)
                .expiryDate(LocalDateTime.now().plusMinutes(AppConstants.OTP_EXPIRY_MINUTES))
                .build();

        mobileOtpRepository.save(otp);
        log.info("OTP generated for user [{}] phone [{}]", user.getEmail(), phoneNumber);
        return rawOtp; // Caller simulates SMS delivery
    }

    @Override
    @Transactional
    public void verifyOtp(String userUuid, String phoneNumber, String rawOtp) {
        User user = userRepository.findByUuidAndIsDeletedFalse(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userUuid));

        MobileOtp otp = mobileOtpRepository.findTopByPhoneNumberAndUsedFalseOrderByCreatedAtDesc(phoneNumber)
                .orElseThrow(() -> new InvalidOtpException("No active OTP found for this phone number"));

        if (!otp.isValid()) {
            throw new OtpExpiredException("OTP has expired. Please request a new one.");
        }

        otp.setAttemptCount(otp.getAttemptCount() + 1);

        if (otp.getAttemptCount() > AppConstants.MAX_OTP_ATTEMPTS) {
            otp.setUsed(Boolean.TRUE);
            mobileOtpRepository.save(otp);
            throw new InvalidOtpException("Maximum OTP attempts exceeded. Please request a new OTP.");
        }

        if (!passwordEncoder.matches(rawOtp, otp.getOtpHash())) {
            mobileOtpRepository.save(otp);
            throw new InvalidOtpException("Invalid OTP. " +
                    (AppConstants.MAX_OTP_ATTEMPTS - otp.getAttemptCount()) + " attempts remaining.");
        }

        otp.setUsed(Boolean.TRUE);
        mobileOtpRepository.save(otp);

        user.setIsMobileVerified(Boolean.TRUE);
        userRepository.save(user);
        log.info("Mobile number verified for user [{}]", user.getEmail());
    }
}
