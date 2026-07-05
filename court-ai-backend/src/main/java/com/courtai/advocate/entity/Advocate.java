package com.courtai.advocate.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Advocate (Lawyer) profile entity.
 *
 * <p>Advocates represent parties in judicial cases.</p>
 */
@Entity
@Table(
        name = "advocate_profiles",
        indexes = {
                @Index(name = "idx_advocate_user_id", columnList = "user_id"),
                @Index(name = "idx_advocate_bar_number", columnList = "bar_council_number")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Advocate extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "bar_council_number", unique = true, length = 100)
    private String barCouncilNumber;

    @Column(name = "law_firm", length = 200)
    private String lawFirm;

    @Column(name = "specialization", length = 200)
    private String specialization;

    @Column(name = "years_of_practice")
    private Integer yearsOfPractice;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = Boolean.FALSE;
}
