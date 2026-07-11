package com.courtai.casecategory.repository;

import com.courtai.casecategory.entity.CaseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository for CaseCategory lookup table. */
@Repository
public interface CaseCategoryRepository extends JpaRepository<CaseCategory, Long> {

    Optional<CaseCategory> findByCategoryCodeAndIsDeletedFalse(String categoryCode);

    Optional<CaseCategory> findByUuidAndIsDeletedFalse(String uuid);

    List<CaseCategory> findByIsActiveTrueAndIsDeletedFalseOrderByDisplayOrderAsc();

    boolean existsByCategoryCode(String categoryCode);
}
