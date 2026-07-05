package com.courtai.judge.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Judge profile entity — extends the base user with judicial-specific attributes.
 *
 * <p>Has a one-to-one relationship with {@link User}.</p>
 *
 * <p>Business attributes (court assignment, case load, specialization)
 * will be populated in Phase 2.</p>
 */
@Entity
@Table(
        name = "judge_profiles",
        indexes = {
                @Index(name = "idx_judge_user_id", columnList = "user_id"),
                @Index(name = "idx_judge_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Judge extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "judge_id_number", unique = true, length = 50)
    private String judgeIdNumber;

    @Column(name = "court_name", length = 200)
    private String courtName;

    @Column(name = "designation", length = 100)
    private String designation;

    @Column(name = "specialization", length = 200)
    private String specialization;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;
}
