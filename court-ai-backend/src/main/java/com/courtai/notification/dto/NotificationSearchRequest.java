package com.courtai.notification.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationSearchRequest {
    private String keyword;
    private Boolean isRead;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    private int page = 0;
    private int size = 20;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
}