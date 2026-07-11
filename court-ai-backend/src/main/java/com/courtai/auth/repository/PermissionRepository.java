package com.courtai.auth.repository;

import com.courtai.auth.entity.Permission;
import com.courtai.common.enums.PermissionCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Permission} entities.
 *
 * <p>Extends standard CRUD with RBAC-specific queries used by the
 * {@link com.courtai.auth.service.PermissionService} layer.</p>
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /** Finds a permission by its unique code. */
    Optional<Permission> findByCode(PermissionCode code);

    /** Finds an active (non-deleted) permission by its code. */
    Optional<Permission> findByCodeAndIsDeletedFalse(PermissionCode code);

    /** Finds an active permission by business UUID. */
    Optional<Permission> findByUuidAndIsDeletedFalse(String uuid);

    /** Lists permissions in a given module (e.g., "CASE"). */
    List<Permission> findByModule(String module);

    /** Lists active permissions in a given module. */
    List<Permission> findByModuleAndIsDeletedFalse(String module);

    /** Lists all active permissions. */
    List<Permission> findAllByIsDeletedFalse();

    /** Checks whether a permission code already exists (active or soft-deleted). */
    boolean existsByCode(PermissionCode code);

    /** Fetches multiple permissions by their codes in one query. */
    List<Permission> findAllByCodeIn(Collection<PermissionCode> codes);
}

