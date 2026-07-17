package com.courtai.notification.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplate extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String title;
    
    @Column
    private String subject;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;
    
    @Column(nullable = false)
    private String channel;
    
    @Column(columnDefinition = "TEXT")
    private String variables;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}