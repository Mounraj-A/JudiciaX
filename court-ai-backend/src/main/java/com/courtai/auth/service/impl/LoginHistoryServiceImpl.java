package com.courtai.auth.service.impl;

import com.courtai.auth.entity.LoginHistory;
import com.courtai.auth.repository.LoginHistoryRepository;
import com.courtai.auth.service.LoginHistoryService;
import com.courtai.common.enums.LoginStatus;
import com.courtai.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginHistoryServiceImpl implements LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public LoginHistory record(User user, LoginStatus status, String ipAddress,
                               String device, String browser, String os, String sessionId) {
        try {
            boolean trusted = isTrustedDevice(user, ipAddress);
            LoginHistory entry = LoginHistory.builder()
                    .user(user)
                    .loginTime(LocalDateTime.now())
                    .ipAddress(ipAddress)
                    .device(device)
                    .browser(browser)
                    .operatingSystem(os)
                    .status(status)
                    .sessionId(sessionId)
                    .isTrustedDevice(trusted)
                    .build();
            return loginHistoryRepository.save(entry);
        } catch (Exception ex) {
            log.error("Failed to record login history: {}", ex.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTrustedDevice(User user, String ipAddress) {
        return loginHistoryRepository.existsByUserAndIpAddressAndStatus(user, ipAddress, LoginStatus.SUCCESS);
    }
}
