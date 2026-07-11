package com.courtai.advocate.repository;

import com.courtai.advocate.entity.Advocate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Repository for Advocate profiles. */
@Repository
public interface AdvocateRepository extends JpaRepository<Advocate, Long> {

    Optional<Advocate> findByUuidAndIsDeletedFalse(String uuid);

    Optional<Advocate> findByBarCouncilNumberAndIsDeletedFalse(String barCouncilNumber);

    boolean existsByBarCouncilNumber(String barCouncilNumber);

    /** Used by AdvocateSecurityUtil to resolve the logged-in user's advocate profile. */
    @Query("SELECT a FROM Advocate a WHERE a.user.uuid = :userUuid AND a.isDeleted = false")
    Optional<Advocate> findByUserUuidAndIsDeletedFalse(@Param("userUuid") String userUuid);

    /** Used for ownership verification via internal user ID. */
    Optional<Advocate> findByUserIdAndIsDeletedFalse(Long userId);
}
