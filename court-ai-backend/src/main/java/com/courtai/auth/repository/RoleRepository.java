package com.courtai.auth.repository;

import com.courtai.auth.entity.Role;
import com.courtai.common.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Role} entities.
 *
 * <p>Extends standard CRUD with RBAC-specific queries used by the
 * {@link com.courtai.auth.service.RolePermissionService} layer.</p>
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /** Finds a role by its enum name (e.g., ROLE_JUDGE). */
    Optional<Role> findByName(UserRole name);

    /** Finds an active (non-deleted) role by enum name. */
    Optional<Role> findByNameAndIsDeletedFalse(UserRole name);

    /** Finds an active role by its business UUID. */
    Optional<Role> findByUuidAndIsDeletedFalse(String uuid);

    /** Lists all active roles. */
    List<Role> findAllByIsDeletedFalse();

    /** Checks whether a role with the given name exists (active or not). */
    boolean existsByName(UserRole name);

    /** Fetches role with permissions eagerly via JPQL join fetch (avoids N+1). */
    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.uuid = :uuid AND r.isDeleted = false")
    Optional<Role> findByUuidWithPermissions(String uuid);
}

