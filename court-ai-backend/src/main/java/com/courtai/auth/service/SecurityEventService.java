package com.courtai.auth.service;

import com.courtai.auth.entity.SecurityEvent;
import com.courtai.auth.entity.SecurityEvent;
import com.courtai.common.enums.SecurityEventType;
import com.courtai.user.entity.User;

/**
 * Service for recording security-relevant events asynchronously.
 *
 * <p>All methods are fire-and-forget — failures are logged but never propagated.</p>
 */
public interface SecurityEventService {

    void record(SecurityEventType type, User user, String description, String ipAddress,
                String device, String browser, String severity);

    void record(SecurityEventType type, String actorEmail, String description,
                String ipAddress, String severity);
}
