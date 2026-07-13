package com.courtai.admin.controller;

import com.courtai.admin.service.AIAdministrationService;
import com.courtai.common.dto.ApiResponse;
import com.courtai.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI administration controller — enable/disable, thresholds, model version.
 * <p>Base path: {@code /api/v1/admin/ai}</p>
 */
@RestController
@RequestMapping("/admin/ai")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin Module — AI Administration", description = "AI engine configuration and usage monitoring")
@SecurityRequirement(name = "bearerAuth")
public class AIAdministrationController {

    private final AIAdministrationService aiService;

    @GetMapping("/settings")
    @Operation(summary = "Get all AI settings",
               description = "Returns AI_ENABLED, model version, priority/confidence thresholds, and explainability flag.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSettings() {
        return ResponseEntity.ok(ApiResponse.success("AI settings retrieved",
                aiService.getAiSettings()));
    }

    @GetMapping("/usage")
    @Operation(summary = "Get AI usage statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUsage() {
        return ResponseEntity.ok(ApiResponse.success("AI usage retrieved",
                aiService.getAiUsageStats()));
    }

    @PutMapping("/enable")
    @Operation(summary = "Enable AI case prioritization")
    public ResponseEntity<ApiResponse<Void>> enable(
            @AuthenticationPrincipal UserPrincipal admin) {
        aiService.enableAi(admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("AI enabled"));
    }

    @PutMapping("/disable")
    @Operation(summary = "Disable AI case prioritization")
    public ResponseEntity<ApiResponse<Void>> disable(
            @AuthenticationPrincipal UserPrincipal admin) {
        aiService.disableAi(admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("AI disabled"));
    }

    @PutMapping("/model-version")
    @Operation(summary = "Update AI model version")
    public ResponseEntity<ApiResponse<Void>> updateModelVersion(
            @RequestParam String version,
            @AuthenticationPrincipal UserPrincipal admin) {
        aiService.updateModelVersion(version, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("Model version updated to: " + version));
    }

    @PutMapping("/priority-threshold")
    @Operation(summary = "Update priority score threshold (0–100)")
    public ResponseEntity<ApiResponse<Void>> updatePriorityThreshold(
            @RequestParam int threshold,
            @AuthenticationPrincipal UserPrincipal admin) {
        aiService.updatePriorityThreshold(threshold, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("Priority threshold updated"));
    }

    @PutMapping("/confidence-threshold")
    @Operation(summary = "Update confidence score threshold (0–100)")
    public ResponseEntity<ApiResponse<Void>> updateConfidenceThreshold(
            @RequestParam int threshold,
            @AuthenticationPrincipal UserPrincipal admin) {
        aiService.updateConfidenceThreshold(threshold, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("Confidence threshold updated"));
    }

    @PutMapping("/explainability")
    @Operation(summary = "Enable or disable XAI (Explainable AI) factor reporting")
    public ResponseEntity<ApiResponse<Void>> setExplainability(
            @RequestParam boolean enabled,
            @AuthenticationPrincipal UserPrincipal admin) {
        aiService.setExplainabilityEnabled(enabled, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("Explainability set to: " + enabled));
    }
}
