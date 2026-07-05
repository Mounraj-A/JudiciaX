package com.courtai.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abstract base entity providing common audit fields for all domain entities.
 *
 * <p>All entities must extend this class to inherit:
 * <ul>
 *   <li>Auto-generated surrogate {@code id} (Long, primary key)</li>
 *   <li>Business-facing {@code uuid} (UUID, unique)</li>
 *   <li>JPA Auditing fields: createdAt, updatedAt, createdBy, updatedBy</li>
 *   <li>Soft-delete flag: {@code isDeleted}</li>
 * </ul>
 *
 * <p>Uses Spring Data JPA Auditing via {@link AuditingEntityListener}.</p>
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * Surrogate primary key — auto-generated Long ID used internally by JPA.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "base_seq")
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * Business-facing UUID — unique, immutable, exposed externally via APIs.
     * Never expose the surrogate {@code id} in public-facing DTOs.
     */
    @Column(name = "uuid", nullable = false, unique = true, updatable = false, length = 36)
    private String uuid;

    /**
     * Timestamp when the record was created. Set automatically by JPA Auditing.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the record was last updated. Set automatically by JPA Auditing.
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Username or system principal who created this record.
     * Populated via {@link com.courtai.config.AuditorAwareImpl}.
     */
    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 100)
    private String createdBy;

    /**
     * Username or system principal who last modified this record.
     */
    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    /**
     * Soft-delete flag. When {@code true}, the record is logically deleted
     * and should be excluded from all standard queries.
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = Boolean.FALSE;

    /**
     * Generates a UUID before the entity is first persisted.
     */
    @PrePersist
    protected void onPrePersist() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
        if (this.isDeleted == null) {
            this.isDeleted = Boolean.FALSE;
        }
    }

    /**
     * Soft-deletes the entity by setting the {@code isDeleted} flag to {@code true}.
     * Call this instead of repository delete methods.
     */
    public void softDelete() {
        this.isDeleted = Boolean.TRUE;
    }
}
