package com.courtai.casefile.repository;

import com.courtai.casefile.entity.ActSectionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActSectionDetailRepository extends JpaRepository<ActSectionDetail, Long> {
    Optional<ActSectionDetail> findByUuid(String uuid);
    List<ActSectionDetail> findByCaseFileId(Long caseFileId);
}
