package com.courtai.clerk.repository;

import com.courtai.clerk.entity.Clerk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Repository for Clerk profiles. */
@Repository
public interface ClerkRepository extends JpaRepository<Clerk, Long> {

    @Query("SELECT c FROM Clerk c WHERE c.user.uuid = :userUuid AND c.isDeleted = false")
    Optional<Clerk> findByUserUuidAndIsDeletedFalse(@Param("userUuid") String userUuid);

    boolean existsByEmployeeId(String employeeId);
}
