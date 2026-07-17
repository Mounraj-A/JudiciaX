package com.courtai.reports.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.reports.dto.response.AIAnalyticsResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.service.AIAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for AI usage and throughput analytics.
 */
@RestController
@RequestMapping("/api/reports/ai")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
@Tag(name = "Reports: AI Analytics", description = "AI pipeline throughput, queue status, and general analytics")
public class AIAnalyticsController {

    private final AIAnalyticsService aiAnalyticsService;

    @GetMapping
    @Operation(summary = "Get full AI analytics summary", description = "High-level metrics on AI pipeline throughput.")
    public ResponseEntity<ApiResponse<AIAnalyticsResponse>> getAIReport() {
        return ResponseEntity.ok(ApiResponse.success(
                "AI report fetched successfully",
                aiAnalyticsService.getAIReport()));
    }

    @GetMapping("/queue-status")
    @Operation(summary = "Get AI queue status breakdown", description = "Current state of the AI processing queue.")
    public ResponseEntity<ApiResponse<List<GraphDataPoint>>> getQueueStatus() {
        return ResponseEntity.ok(ApiResponse.success(
                "Queue status fetched successfully",
                aiAnalyticsService.getQueueStatusBreakdown()));
    }
}
