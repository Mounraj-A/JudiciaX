package com.courtai.payment.entity;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.common.entity.BaseEntity;
import com.courtai.advocate.entity.Advocate;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "case_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CasePayment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advocate_id", nullable = false)
    private Advocate advocate;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_status", nullable = false, length = 50)
    private String paymentStatus; // e.g. PENDING, COMPLETED, FAILED

    @Column(name = "payment_method", length = 50)
    private String paymentMethod; // e.g. CREDIT_CARD, UPI, NET_BANKING

    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    @Column(name = "receipt_url", length = 500)
    private String receiptUrl;
}
