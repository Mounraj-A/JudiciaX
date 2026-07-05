package com.courtai.clerk.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Court Clerk profile entity.
 *
 * <p>Clerks are responsible for case filing, document management, and scheduling.</p>
 */
@Entity
@Table(
        name = "clerk_profiles",
        indexes = {
                @Index(name = "idx_clerk_user_id", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clerk extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "employee_id", unique = true, length = 50)
    private String employeeId;

    @Column(name = "court_section", length = 100)
    private String courtSection;

    @Column(name = "department", length = 100)
    private String department;
}
