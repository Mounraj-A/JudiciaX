package com.courtai.judge.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.judge.dto.JudgeOrderRequest;
import com.courtai.judge.dto.JudgeOrderResponse;
import com.courtai.judge.service.JudgeOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Order management controller for the judge portal.
 * Handles interim orders, final orders, and judgments.
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_JUDGE')")
@Tag(name = "Judge Module", description = "Judge Portal — Order Management")
public class JudgeOrderController {

    private final JudgeOrderService orderService;

    // ── Case-scoped order endpoints ───────────────────────────────────────────

    @PostMapping("/judge/cases/{caseUuid}/orders")
    @Operation(summary = "Upload a judicial order",
               description = "Uploads an interim order, final order, or judgment for a case. "
                           + "Metadata only — binary file uploaded directly to storage.")
    public ResponseEntity<ApiResponse<JudgeOrderResponse>> createOrder(
            @PathVariable String caseUuid,
            @Valid @RequestBody JudgeOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Order uploaded",
                        orderService.createOrder(caseUuid, request)));
    }

    @GetMapping("/judge/cases/{caseUuid}/orders")
    @Operation(summary = "List orders for a case")
    public ResponseEntity<ApiResponse<List<JudgeOrderResponse>>> getOrdersByCase(
            @PathVariable String caseUuid) {
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved",
                orderService.getOrdersByCase(caseUuid)));
    }

    // ── Flat order endpoints ──────────────────────────────────────────────────

    @GetMapping("/judge/orders/{orderUuid}")
    @Operation(summary = "Get order by UUID")
    public ResponseEntity<ApiResponse<JudgeOrderResponse>> getOrder(
            @PathVariable String orderUuid) {
        return ResponseEntity.ok(ApiResponse.success("Order retrieved",
                orderService.getOrderByUuid(orderUuid)));
    }

    @PutMapping("/judge/orders/{orderUuid}")
    @Operation(summary = "Update an order",
               description = "Updates an unsigned judicial order. Signed orders are immutable.")
    public ResponseEntity<ApiResponse<JudgeOrderResponse>> updateOrder(
            @PathVariable String orderUuid,
            @Valid @RequestBody JudgeOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Order updated",
                orderService.updateOrder(orderUuid, request)));
    }
}
