package com.courtai.advocate.controller;

import com.courtai.advocate.util.AdvocateSecurityUtil;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.common.dto.ApiResponse;
import com.courtai.exception.UnauthorizedActionException;
import com.courtai.judge.dto.JudgeOrderResponse;
import com.courtai.judge.entity.JudgeOrder;
import com.courtai.judge.mapper.JudgeOrderMapper;
import com.courtai.judge.repository.JudgeOrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/advocate/cases/{caseUuid}/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADVOCATE')")
@Tag(name = "Advocate Module")
public class AdvocateOrderController {

    private final AdvocateSecurityUtil securityUtil;
    private final CaseFileRepository caseFileRepository;
    private final JudgeOrderRepository orderRepository;
    private final JudgeOrderMapper orderMapper;

    @GetMapping
    @Operation(summary = "List orders for a case")
    public ResponseEntity<ApiResponse<Page<JudgeOrderResponse>>> getOrders(
            @PathVariable String caseUuid,
            Pageable pageable) {
        
        var advocate = securityUtil.getCurrentAdvocate();
        if (!caseFileRepository.existsByUuidAndAdvocateUuid(caseUuid, advocate.getUuid())) {
            throw new UnauthorizedActionException("Case does not belong to your account.");
        }

        Page<JudgeOrder> orders = orderRepository.findByCaseFileUuidAndOrderTypeInAndIsDeletedFalseOrderByOrderDateDesc(
                caseUuid, java.util.List.of("INTERIM_ORDER", "FINAL_ORDER"), pageable);
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved", orders.map(orderMapper::toResponse)));
    }

    @GetMapping("/judgements")
    @Operation(summary = "List judgements for a case")
    public ResponseEntity<ApiResponse<Page<JudgeOrderResponse>>> getJudgements(
            @PathVariable String caseUuid,
            Pageable pageable) {
        
        var advocate = securityUtil.getCurrentAdvocate();
        if (!caseFileRepository.existsByUuidAndAdvocateUuid(caseUuid, advocate.getUuid())) {
            throw new UnauthorizedActionException("Case does not belong to your account.");
        }

        Page<JudgeOrder> judgements = orderRepository.findByCaseFileUuidAndOrderTypeInAndIsDeletedFalseOrderByOrderDateDesc(
                caseUuid, java.util.List.of("JUDGMENT"), pageable);
        return ResponseEntity.ok(ApiResponse.success("Judgements retrieved", judgements.map(orderMapper::toResponse)));
    }
}
