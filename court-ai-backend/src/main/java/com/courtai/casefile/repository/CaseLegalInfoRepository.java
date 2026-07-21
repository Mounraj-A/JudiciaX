package com.courtai.casefile.repository;

import com.courtai.casefile.entity.CaseLegalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaseLegalInfoRepository extends JpaRepository<CaseLegalInfo, Long> {
    Optional<CaseLegalInfo> findByCaseFileUuid(String caseUuid);
    Optional<CaseLegalInfo> findByCaseFileId(Long caseId);
}
