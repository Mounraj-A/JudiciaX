package com.courtai.user.repository;

import com.courtai.common.enums.AccountStatus;
import com.courtai.common.enums.UserRole;
import com.courtai.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for the {@link User} entity.
 *
 * <p>All standard queries exclude soft-deleted records via {@code is_deleted = false}.
 * Admin queries may include deleted records by using different method names.</p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndIsDeletedFalse(String email);
    Optional<User> findByUuidAndIsDeletedFalse(String uuid);
    Optional<User> findByPhoneNumberAndIsDeletedFalse(String phoneNumber);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByFullName(String fullName);

    List<User> findByRoleAndIsDeletedFalse(UserRole role);
    List<User> findAllByIsDeletedFalse();
    List<User> findByAccountStatusAndIsDeletedFalse(AccountStatus accountStatus);

    @Query("SELECT u FROM User u WHERE (u.username = :identifier OR u.email = :identifier) AND u.isDeleted = false")
    Optional<User> findByUsernameOrEmailAndIsDeletedFalse(@Param("identifier") String identifier);

    /** Count users registered between two timestamps. */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :from AND :to AND u.isDeleted = false")
    long countByCreatedAtBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /** Find accounts with timed locks that have now expired — for auto-unlock scheduler. */
    @Query("SELECT u FROM User u WHERE u.accountStatus = 'LOCKED' AND u.accountLockedUntil IS NOT NULL AND u.accountLockedUntil < :now")
    List<User> findExpiredLockedAccounts(@Param("now") LocalDateTime now);

    /** Count accounts currently pending verification. */
    long countByAccountStatusAndIsDeletedFalse(AccountStatus status);

    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :time WHERE u.id = :id")
    void updateLastLogin(@Param("id") Long id, @Param("time") LocalDateTime time);
}
