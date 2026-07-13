package com.courtai.admin.dto;

import com.courtai.common.enums.AccountStatus;
import com.courtai.common.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/** Compact user summary for admin listing views. */
@Getter
@Builder
public class UserSummaryResponse {
    private String uuid;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private AccountStatus accountStatus;
    private Boolean isEmailVerified;
    private Boolean isLocked;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
}
