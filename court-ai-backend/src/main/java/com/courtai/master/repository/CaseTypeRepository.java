package com.courtai.master.repository;

import com.courtai.master.entity.CaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseTypeRepository extends JpaRepository<CaseType, Long> {

    Optional<CaseType> findByUuidAndIsDeletedFalse(String uuid);

    Optional<CaseType> findByTypeCodeAndIsDeletedFalse(String typeCode);

    List<CaseType> findByIsActiveTrueAndIsDeletedFalseOrderByDisplayOrderAsc();

    boolean existsByTypeCode(String typeCode);
}
