package com.courtai.admin.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class AnnouncementRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    /** LOW, MEDIUM, HIGH, CRITICAL */
    private String priority;

    /** ROLE_JUDGE, ROLE_CLERK, ROLE_ADVOCATE, ROLE_ADMIN, ALL */
    private String targetRole;

    @FutureOrPresent
    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isActive;
}
