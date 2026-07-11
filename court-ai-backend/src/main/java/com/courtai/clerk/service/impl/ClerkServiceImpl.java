package com.courtai.clerk.service.impl;

import com.courtai.clerk.dto.ClerkProfileResponse;
import com.courtai.clerk.entity.Clerk;
import com.courtai.clerk.service.ClerkService;
import com.courtai.clerk.util.ClerkSecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of {@link ClerkService}. */
@Service
@RequiredArgsConstructor
public class ClerkServiceImpl implements ClerkService {

    private final ClerkSecurityUtil securityUtil;

    @Override
    @Transactional(readOnly = true)
    public ClerkProfileResponse getMyProfile() {
        Clerk clerk = securityUtil.getCurrentClerk();
        return ClerkProfileResponse.builder()
                .uuid(clerk.getUuid())
                .fullName(clerk.getUser().getFullName())
                .email(clerk.getUser().getEmail())
                .employeeId(clerk.getEmployeeId())
                .courtSection(clerk.getCourtSection())
                .department(clerk.getDepartment())
                .courtUuid(clerk.getCourt() != null ? clerk.getCourt().getUuid() : null)
                .courtName(clerk.getCourt() != null ? clerk.getCourt().getCourtName() : null)
                .courtCode(clerk.getCourt() != null ? clerk.getCourt().getCourtCode() : null)
                .build();
    }
}
