package com.courtai.admin.service;

import com.courtai.admin.dto.AssignClerkRequest;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/** Admin service for clerk administration — assignment, statistics. */
public interface ClerkAdministrationService {
    void assignClerkToCourt(AssignClerkRequest request, String adminUuid);
    void transferClerk(String clerkUserUuid, String newCourtUuid, String reason, String adminUuid);
    Map<String, Object> getClerkStatistics(String clerkUserUuid);
}
