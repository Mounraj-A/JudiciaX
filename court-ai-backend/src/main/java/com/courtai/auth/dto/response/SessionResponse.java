package com.courtai.auth.dto.response;

import com.courtai.common.enums.SessionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Session information returned in GET /users/me/sessions.
 */
@Getter
@Builder
public class SessionResponse {
    private final String uuid;
    private final String device;
    private final String browser;
    private final String operatingSystem;
    private final String ipAddress;
    private final Boolean isTrustedDevice;
    private final SessionStatus status;
    private final Boolean isCurrent;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime loginTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime lastActivityAt;
}
