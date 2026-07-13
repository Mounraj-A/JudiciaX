package com.courtai.admin.service.impl;

import com.courtai.admin.dto.AssignClerkRequest;
import com.courtai.admin.service.ClerkAdministrationService;
import com.courtai.audit.service.AuditService;
import com.courtai.clerk.entity.Clerk;
import com.courtai.clerk.repository.ClerkRepository;
import com.courtai.court.entity.Court;
import com.courtai.court.repository.CourtRepository;
import com.courtai.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClerkAdministrationServiceImpl implements ClerkAdministrationService {

    private final ClerkRepository  clerkRepository;
    private final CourtRepository  courtRepository;
    private final AuditService     auditService;

    @Override
    @Transactional
    public void assignClerkToCourt(AssignClerkRequest request, String adminUuid) {
        Court court = courtRepository.findByUuidAndIsDeletedFalse(request.getCourtUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Court", "uuid", request.getCourtUuid()));
        Clerk clerk = clerkRepository.findByUserUuidAndIsDeletedFalse(request.getClerkUserUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Clerk", "userUuid", request.getClerkUserUuid()));

        clerk.setCourt(court);
        clerkRepository.save(clerk);

        auditService.logSuccess("CLERK_ASSIGNED", "Clerk", clerk.getUuid(),
                "Clerk " + clerk.getUuid() + " assigned to court " + court.getUuid()
                        + " by admin " + adminUuid);
    }

    @Override
    @Transactional
    public void transferClerk(String clerkUserUuid, String newCourtUuid, String reason, String adminUuid) {
        Clerk clerk = clerkRepository.findByUserUuidAndIsDeletedFalse(clerkUserUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Clerk", "userUuid", clerkUserUuid));
        Court newCourt = courtRepository.findByUuidAndIsDeletedFalse(newCourtUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Court", "uuid", newCourtUuid));

        String oldCourtUuid = clerk.getCourt() != null
                ? clerk.getCourt().getUuid() : "NONE";
        clerk.setCourt(newCourt);
        clerkRepository.save(clerk);

        auditService.logSuccess("CLERK_TRANSFERRED", "Clerk", clerk.getUuid(),
                "Clerk transferred from court " + oldCourtUuid + " to " + newCourtUuid
                        + " by admin " + adminUuid + ". Reason: " + reason);
    }

    @Override
    public Map<String, Object> getClerkStatistics(String clerkUserUuid) {
        Clerk clerk = clerkRepository.findByUserUuidAndIsDeletedFalse(clerkUserUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Clerk", "userUuid", clerkUserUuid));

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("clerkUuid",    clerk.getUuid());
        stats.put("employeeId",   clerk.getEmployeeId());
        stats.put("clerkName",    clerk.getUser() != null ? clerk.getUser().getDisplayName() : "N/A");
        stats.put("assignedCourt", clerk.getCourt() != null
                ? clerk.getCourt().getCourtName() : "UNASSIGNED");
        return stats;
    }
}
