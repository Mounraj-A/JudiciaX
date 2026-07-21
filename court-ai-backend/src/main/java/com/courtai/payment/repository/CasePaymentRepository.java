package com.courtai.payment.repository;

import com.courtai.payment.entity.CasePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CasePaymentRepository extends JpaRepository<CasePayment, Long> {
    Optional<CasePayment> findByUuid(String uuid);
    Optional<CasePayment> findByCaseFileUuid(String caseUuid);
}
