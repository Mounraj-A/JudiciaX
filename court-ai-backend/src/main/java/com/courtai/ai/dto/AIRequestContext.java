package com.courtai.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Enterprise context passed along with every AI request.
 * Ensures complete traceability.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIRequestContext {
    private String correlationId;
    private String requestId;
    private String userUuid;
    private String userRole;
    private String caseUuid;
    private String courtUuid;
    private String documentUuid; // optional
    private String sessionId;
    private LocalDateTime timestamp;
    private String locale;
    private String apiVersion;
    private String clientVersion;
}
