package com.courtai.judge.repository;

import com.courtai.judge.entity.JudgeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository for JudgeOrder — judicial orders and judgments. */
@Repository
public interface JudgeOrderRepository extends JpaRepository<JudgeOrder, Long> {

    /** All non-deleted orders for a given case (by DB id). */
    List<JudgeOrder> findByCaseFileIdAndIsDeletedFalseOrderByOrderDateDesc(Long caseFileId);

    /** Paginated non-deleted orders for a given case (by UUID). */
    org.springframework.data.domain.Page<JudgeOrder> findByCaseFileUuidAndIsDeletedFalseOrderByOrderDateDesc(String uuid, org.springframework.data.domain.Pageable pageable);

    /** Paginated non-deleted orders for a given case (by UUID) filtered by order types. */
    org.springframework.data.domain.Page<JudgeOrder> findByCaseFileUuidAndOrderTypeInAndIsDeletedFalseOrderByOrderDateDesc(String uuid, java.util.Collection<String> orderTypes, org.springframework.data.domain.Pageable pageable);

    /** All non-deleted orders for a case filtered by order type. */
    List<JudgeOrder> findByCaseFileIdAndOrderTypeAndIsDeletedFalse(Long caseFileId, String orderType);

    /** Lookup by UUID. */
    Optional<JudgeOrder> findByUuidAndIsDeletedFalse(String uuid);

    /** All orders authored by a specific judge. */
    @Query("SELECT o FROM JudgeOrder o WHERE o.judge.uuid = :judgeUuid AND o.isDeleted = false ORDER BY o.orderDate DESC")
    List<JudgeOrder> findByJudgeUuidAndIsDeletedFalse(@Param("judgeUuid") String judgeUuid);

    /** Count of orders for a case — used for dashboard. */
    long countByCaseFileIdAndIsDeletedFalse(Long caseFileId);
}
