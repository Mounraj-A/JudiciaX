package com.courtai.judge.service.impl;

import com.courtai.audit.service.AuditService;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.common.enums.CaseStatus;
import com.courtai.exception.BusinessRuleViolationException;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import com.courtai.judge.dto.JudgeOrderRequest;
import com.courtai.judge.dto.JudgeOrderResponse;
import com.courtai.judge.entity.Judge;
import com.courtai.judge.entity.JudgeOrder;
import com.courtai.judge.mapper.JudgeOrderMapper;
import com.courtai.judge.repository.JudgeOrderRepository;
import com.courtai.judge.service.JudgeNotificationService;
import com.courtai.judge.service.JudgeOrderService;
import com.courtai.judge.service.JudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link JudgeOrderService}.
 */
@Service
@RequiredArgsConstructor
public class JudgeOrderServiceImpl implements JudgeOrderService {

    private final JudgeService          judgeService;
    private final JudgeOrderRepository  orderRepository;
    private final CaseFileRepository    caseFileRepository;
    private final JudgeOrderMapper      orderMapper;
    private final AuditService          auditService;
    private final JudgeNotificationService notificationService;

    @Override
    @Transactional
    public JudgeOrderResponse createOrder(String caseUuid, JudgeOrderRequest request) {
        Judge    judge    = judgeService.getCurrentJudge();
        CaseFile caseFile = getAssignedCase(caseUuid, judge);

        // Business rule: cannot upload order if case is DISPOSED
        // (unless the order is a JUDGMENT which triggered disposal)
        if (caseFile.getStatus() == CaseStatus.DISPOSED
                && !"JUDGMENT".equalsIgnoreCase(request.getOrderType())) {
            throw new BusinessRuleViolationException(
                    "Cannot upload orders after a case has been disposed: " + caseUuid);
        }

        JudgeOrder order = JudgeOrder.builder()
                .caseFile(caseFile)
                .judge(judge)
                .orderType(request.getOrderType().toUpperCase())
                .title(request.getTitle())
                .orderText(request.getOrderText())
                .orderDate(request.getOrderDate() != null ? request.getOrderDate() : LocalDate.now())
                .storagePath(request.getStoragePath())
                .originalFileName(request.getOriginalFileName())
                .mimeType(request.getMimeType())
                .fileSizeBytes(request.getFileSizeBytes())
                .isSigned(request.getIsSigned() != null ? request.getIsSigned() : Boolean.FALSE)
                .remarks(request.getRemarks())
                .build();

        JudgeOrder saved = orderRepository.save(order);

        auditService.logSuccess("ORDER_UPLOADED", "JudgeOrder", saved.getUuid(),
                "Judge " + judge.getUuid() + " uploaded " + request.getOrderType()
                        + " for case " + caseUuid);

        notificationService.notifyOrderUploaded(caseFile, saved);

        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JudgeOrderResponse> getOrdersByCase(String caseUuid) {
        Judge    judge    = judgeService.getCurrentJudge();
        CaseFile caseFile = getAssignedCase(caseUuid, judge);

        return orderRepository
                .findByCaseFileIdAndIsDeletedFalseOrderByOrderDateDesc(caseFile.getId())
                .stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public JudgeOrderResponse getOrderByUuid(String orderUuid) {
        Judge judge = judgeService.getCurrentJudge();
        JudgeOrder order = orderRepository.findByUuidAndIsDeletedFalse(orderUuid)
                .orElseThrow(() -> new ResourceNotFoundException("JudgeOrder", "uuid", orderUuid));
        validateOrderBelongsToJudge(order, judge);
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public JudgeOrderResponse updateOrder(String orderUuid, JudgeOrderRequest request) {
        Judge judge = judgeService.getCurrentJudge();
        JudgeOrder order = orderRepository.findByUuidAndIsDeletedFalse(orderUuid)
                .orElseThrow(() -> new ResourceNotFoundException("JudgeOrder", "uuid", orderUuid));
        validateOrderBelongsToJudge(order, judge);

        // Business rule: cannot modify a signed order
        if (Boolean.TRUE.equals(order.getIsSigned())) {
            throw new BusinessRuleViolationException(
                    "Cannot modify a signed judicial order: " + orderUuid);
        }

        if (request.getTitle()            != null) order.setTitle(request.getTitle());
        if (request.getOrderText()        != null) order.setOrderText(request.getOrderText());
        if (request.getOrderDate()        != null) order.setOrderDate(request.getOrderDate());
        if (request.getStoragePath()      != null) order.setStoragePath(request.getStoragePath());
        if (request.getOriginalFileName() != null) order.setOriginalFileName(request.getOriginalFileName());
        if (request.getMimeType()         != null) order.setMimeType(request.getMimeType());
        if (request.getFileSizeBytes()    != null) order.setFileSizeBytes(request.getFileSizeBytes());
        if (request.getIsSigned()         != null) order.setIsSigned(request.getIsSigned());
        if (request.getRemarks()          != null) order.setRemarks(request.getRemarks());

        return orderMapper.toResponse(orderRepository.save(order));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CaseFile getAssignedCase(String caseUuid, Judge judge) {
        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));
        if (caseFile.getAssignedJudge() == null
                || !judge.getUuid().equals(caseFile.getAssignedJudge().getUuid())) {
            throw new UnauthorizedActionException("Case is not assigned to you: " + caseUuid);
        }
        return caseFile;
    }

    private void validateOrderBelongsToJudge(JudgeOrder order, Judge judge) {
        if (!judge.getUuid().equals(order.getJudge().getUuid())) {
            throw new UnauthorizedActionException("This order does not belong to you: " + order.getUuid());
        }
    }
}
