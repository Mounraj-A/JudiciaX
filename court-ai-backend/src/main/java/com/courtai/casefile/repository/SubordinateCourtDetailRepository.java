package com.courtai.casefile.repository;

import com.courtai.casefile.entity.SubordinateCourtDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubordinateCourtDetailRepository extends JpaRepository<SubordinateCourtDetail, Long> {
    Optional<SubordinateCourtDetail> findByUuid(String uuid);
    List<SubordinateCourtDetail> findByCaseFileId(Long caseFileId);
}
