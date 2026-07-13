package com.courtai.judge.service;

import com.courtai.judge.dto.JudgeOrderRequest;
import com.courtai.judge.dto.JudgeOrderResponse;

import java.util.List;

/**
 * Service contract for judge order management.
 * Manages interim orders, final orders, and judgments.
 */
public interface JudgeOrderService {

    /** Uploads a new judicial order for a case. */
    JudgeOrderResponse createOrder(String caseUuid, JudgeOrderRequest request);

    /** Returns all orders for a case. */
    List<JudgeOrderResponse> getOrdersByCase(String caseUuid);

    /** Returns a specific order by UUID. */
    JudgeOrderResponse getOrderByUuid(String orderUuid);

    /** Updates an existing order (only if not yet signed). */
    JudgeOrderResponse updateOrder(String orderUuid, JudgeOrderRequest request);
}
