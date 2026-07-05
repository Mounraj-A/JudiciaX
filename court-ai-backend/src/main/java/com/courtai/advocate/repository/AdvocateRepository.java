package com.courtai.advocate.repository;

import com.courtai.advocate.entity.Advocate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Repository for Advocate profiles. */
@Repository
public interface AdvocateRepository extends JpaRepository<Advocate, Long> {
    Optional<Advocate> findByUuidAndIsDeletedFalse(String uuid);
    Optional<Advocate> findByBarCouncilNumberAndIsDeletedFalse(String barCouncilNumber);
    boolean existsByBarCouncilNumber(String barCouncilNumber);
}
