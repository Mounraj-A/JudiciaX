package com.courtai.casemanagement.repository;

import com.courtai.casemanagement.entity.CaseTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Repository for {@link CaseTransfer} records. */
@Repository
public interface CaseTransferRepository extends JpaRepository<CaseTransfer, Long> {

    @Query("SELECT t FROM CaseTransfer t WHERE t.caseFile.uuid = :caseUuid " +
           "AND t.isDeleted = false ORDER BY t.transferredAt DESC")
    List<CaseTransfer> findByCaseUuidOrderByTransferredAtDesc(@Param("caseUuid") String caseUuid);

    @Query("SELECT t FROM CaseTransfer t WHERE t.caseFile.uuid = :caseUuid " +
           "AND t.transferType = :type AND t.isDeleted = false ORDER BY t.transferredAt DESC")
    List<CaseTransfer> findByCaseUuidAndTransferType(@Param("caseUuid") String caseUuid,
                                                      @Param("type") String type);
}
