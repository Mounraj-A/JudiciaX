package com.courtai.court.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Represents a physical courtroom within a {@link Court}.
 *
 * <p>Hearings are scheduled in specific courtrooms. Courtrooms
 * can be assigned to specific benches or remain general-purpose.</p>
 */
@Entity
@Table(
        name = "court_rooms",
        indexes = {
                @Index(name = "idx_room_court_id",   columnList = "court_id"),
                @Index(name = "idx_room_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtRoom extends BaseEntity {

    /** The court building this room belongs to. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    /** Room number or identifier — e.g., "COURT-3", "ROOM-101". */
    @NotBlank
    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;

    /** Floor where the room is located. */
    @Column(name = "floor", length = 20)
    private String floor;

    /** Seating capacity of the courtroom. */
    @Column(name = "capacity")
    private Integer capacity;

    /** Whether video conferencing equipment is available. */
    @Column(name = "has_video_conferencing", nullable = false)
    @Builder.Default
    private Boolean hasVideoConferencing = Boolean.FALSE;

    /** Whether this room is currently in service. */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;
}
