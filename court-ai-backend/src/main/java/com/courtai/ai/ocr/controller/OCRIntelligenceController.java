package com.courtai.ai.ocr.controller;

import com.courtai.ai.ocr.dto.*;
import com.courtai.ai.ocr.benchmark.OCRBenchmarkService;
import com.courtai.ai.ocr.decision.OCRDecisionService;
import com.courtai.ai.ocr.monitor.OCRMonitoringService;
import com.courtai.ai.ocr.profile.OCRProfileService;
import com.courtai.ai.ocr.quality.OCRQualityService;
import com.courtai.ai.ocr.queue.OCRQueueService;
import com.courtai.ai.ocr.routing.OCRRoutingService;
import com.courtai.ai.ocr.service.OCRGovernanceService;
import com.courtai.ai.ocr.validator.OCRValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai/ocr")
@RequiredArgsConstructor
@Tag(name = "OCR Intelligence Engine", description = "Enterprise OCR Orchestration API")
public class OCRIntelligenceController {

    private final OCRProfileService profileService;
    private final OCRDecisionService decisionService;
    private final OCRRoutingService routingService;
    private final OCRValidationService validationService;
    private final OCRQualityService qualityService;
    private final OCRQueueService queueService;
    private final OCRMonitoringService monitoringService;
    private final OCRBenchmarkService benchmarkService;
    private final OCRGovernanceService governanceService;

    @PostMapping("/process")
    @Operation(summary = "Execute full OCR Intelligence workflow")
    public ResponseEntity<OCRWorkflowResponse> processOcrWorkflow(@ModelAttribute OCRProfileRequest request) {
        return ResponseEntity.ok(governanceService.processOCRWorkflow(request));
    }

    @PostMapping("/profile")
    @Operation(summary = "Generate OCR Profile")
    public ResponseEntity<OCRProfileResponse> generateProfile(@ModelAttribute OCRProfileRequest request) {
        return ResponseEntity.ok(profileService.generateProfile(request));
    }
    
    @GetMapping("/profile/{uuid}")
    @Operation(summary = "Get OCR Profile by Document UUID")
    public ResponseEntity<OCRProfileResponse> getProfile(@PathVariable String uuid) {
        // Since we are simulating, we construct a dummy request to generate a profile
        OCRProfileRequest req = OCRProfileRequest.builder().documentUuid(uuid).build();
        return ResponseEntity.ok(profileService.generateProfile(req));
    }

    @PostMapping("/decision")
    @Operation(summary = "Generate OCR Decision")
    public ResponseEntity<OCRDecisionResponse> getDecision(@RequestBody OCRProfileResponse profile) {
        OCRQualityResponse quality = qualityService.assessQuality(profile);
        return ResponseEntity.ok(decisionService.determineDecision(profile, quality, "SIMULATED_POLICY"));
    }

    @PostMapping("/route")
    @Operation(summary = "Determine OCR Route")
    public ResponseEntity<String> getRoute(@RequestBody OCRProfileResponse profile) {
        OCRQualityResponse quality = qualityService.assessQuality(profile);
        OCRDecisionResponse decision = decisionService.determineDecision(profile, quality, "SIMULATED_POLICY");
        return ResponseEntity.ok(routingService.determineRoute(profile, decision));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate OCR Results")
    public ResponseEntity<OCRValidationResponse> validateOcr(@RequestBody OCRProfileResponse profile) {
        return ResponseEntity.ok(validationService.validateOCRResult(profile));
    }

    @PostMapping("/quality")
    @Operation(summary = "Assess Document OCR Quality")
    public ResponseEntity<OCRQualityResponse> assessQuality(@RequestBody OCRProfileResponse profile) {
        return ResponseEntity.ok(qualityService.assessQuality(profile));
    }

    @GetMapping("/queue")
    @Operation(summary = "Get Document Queue Status")
    public ResponseEntity<OCRQueueResponse> getQueueStatus(@RequestParam String documentUuid) {
        return ResponseEntity.ok(queueService.getQueueState(documentUuid));
    }

    @GetMapping("/monitor")
    @Operation(summary = "Get OCR Telemetry and Health")
    public ResponseEntity<OCRMonitoringResponse> getMonitoring() {
        return ResponseEntity.ok(monitoringService.getMonitoringMetrics());
    }

    @GetMapping("/benchmark")
    @Operation(summary = "Get OCR Benchmark Statistics")
    public ResponseEntity<OCRBenchmarkResponse> getBenchmark() {
        return ResponseEntity.ok(benchmarkService.getBenchmarkMetrics());
    }
}
