package com.courtai.user.repository;

import com.courtai.user.entity.User;
import com.courtai.common.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for the {@link User} entity providing standard JPA operations
 * and custom query methods.
 *
 * <p>All queries automatically exclude soft-deleted records via {@code is_deleted = false} conditions.</p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds an active (non-deleted) user by email.
     */
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    /**
     * Finds an active user by UUID (the externally exposed identifier).
     */
    Optional<User> findByUuidAndIsDeletedFalse(String uuid);

    /**
     * Checks whether an email already exists in the system (including soft-deleted).
     */
    boolean existsByEmail(String email);

    /**
     * Checks whether a username already exists.
     */
    boolean existsByUsername(String username);

    /**
     * Finds all active users with a specific role.
     */
    List<User> findByRoleAndIsDeletedFalse(UserRole role);

    /**
     * Finds all active users.
     */
    List<User> findAllByIsDeletedFalse();

    /**
     * Custom JPQL query to find an active user by username or email.
     */
    @Query("SELECT u FROM User u WHERE (u.username = :identifier OR u.email = :identifier) AND u.isDeleted = false")
    Optional<User> findByUsernameOrEmailAndIsDeletedFalse(@Param("identifier") String identifier);
}
