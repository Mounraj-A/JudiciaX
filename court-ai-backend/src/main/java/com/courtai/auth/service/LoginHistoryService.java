package com.courtai.auth.service;

import com.courtai.auth.entity.LoginHistory;
import com.courtai.user.entity.User;
import com.courtai.common.enums.LoginStatus;

/**
 * Records login history entries for auditing and device recognition.
 */
public interface LoginHistoryService {

    /** Saves a new login history record. */
    LoginHistory record(User user, LoginStatus status, String ipAddress,
                        String device, String browser, String os, String sessionId);

    /** Returns {@code true} if this IP/device has been used for a successful login before. */
    boolean isTrustedDevice(User user, String ipAddress);
}
