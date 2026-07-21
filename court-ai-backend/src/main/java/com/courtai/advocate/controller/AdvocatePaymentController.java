package com.courtai.advocate.controller;

import com.courtai.advocate.dto.PaymentRequest;
import com.courtai.common.dto.ApiResponse;
import com.courtai.payment.entity.CasePayment;
import com.courtai.payment.repository.CasePaymentRepository;
import com.courtai.advocate.util.AdvocateSecurityUtil;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@RestController
@RequestMapping("/advocate/payments")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADVOCATE')")
@Tag(name = "Advocate Payment Module")
public class AdvocatePaymentController {

    private final CasePaymentRepository paymentRepository;
    private final CaseFileRepository caseFileRepository;
    private final AdvocateSecurityUtil securityUtil;

    @PostMapping
    @Transactional
    @Operation(summary = "Process payment for a case")
    public ResponseEntity<ApiResponse<String>> processPayment(@Valid @RequestBody PaymentRequest request) {
        var advocate = securityUtil.getCurrentAdvocate();
        
        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(request.getCaseUuid())
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", request.getCaseUuid()));

        if (!caseFileRepository.existsByUuidAndAdvocateUuid(request.getCaseUuid(), advocate.getUuid())) {
            throw new com.courtai.exception.UnauthorizedActionException("Case does not belong to you.");
        }

        // Mock payment processing
        CasePayment payment = CasePayment.builder()
                .caseFile(caseFile)
                .advocate(advocate)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus("COMPLETED")
                .transactionId("TRX-" + UUID.randomUUID().toString().substring(0,8).toUpperCase())
                .receiptUrl("/receipts/" + UUID.randomUUID() + ".pdf")
                .build();
                
        paymentRepository.save(payment);

        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", payment.getTransactionId()));
    }
}
