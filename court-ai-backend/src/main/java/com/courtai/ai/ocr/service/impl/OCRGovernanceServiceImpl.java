package com.courtai.ai.ocr.service.impl;

import com.courtai.ai.ocr.dataset.OCRDatasetService;
import com.courtai.ai.ocr.decision.OCRDecisionService;
import com.courtai.ai.ocr.dto.*;
import com.courtai.ai.ocr.model.OCRQueueState;
import com.courtai.ai.ocr.monitor.OCRMonitoringService;
import com.courtai.ai.ocr.profile.OCRProfileService;
import com.courtai.ai.ocr.quality.OCRQualityService;
import com.courtai.ai.ocr.queue.OCRQueueService;
import com.courtai.ai.ocr.routing.OCRRoutingService;
import com.courtai.ai.ocr.service.OCRGovernanceService;
import com.courtai.ai.ocr.strategy.OCRStrategyService;
import com.courtai.ai.ocr.validator.OCRValidationService;
import com.courtai.audit.service.AuditService;
import com.courtai.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OCRGovernanceServiceImpl implements OCRGovernanceService {

    private final OCRProfileService profileService;
    private final OCRStrategyService strategyService;
    private final OCRQualityService qualityService;
    private final OCRDecisionService decisionService;
    private final OCRQueueService queueService;
    private final OCRValidationService validationService;
    private final OCRRoutingService routingService;
    private final OCRMonitoringService monitoringService;
    private final OCRDatasetService datasetService;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Override
    public OCRWorkflowResponse processOCRWorkflow(OCRProfileRequest request) {
        long startTime = System.currentTimeMillis();
        String docUuid = request.getDocumentUuid();
        String userUuid = request.getUserUuid();
        
        try {
            // 1. Profile Generation
            OCRProfileResponse profile = profileService.generateProfile(request);
            auditService.logSuccess("OCR_PROFILE_CREATED", "Document", docUuid, "OCR Profile generated successfully.");
            
            // 2. Strategy & Policy
            String strategyPolicy = strategyService.determinePolicy(profile);
            
            // 3. Quality Assessment
            OCRQualityResponse quality = qualityService.assessQuality(profile);
            
            // 4. Decision Engine
            OCRDecisionResponse decision = decisionService.determineDecision(profile, quality, strategyPolicy);
            auditService.logSuccess("OCR_DECISION_GENERATED", "Document", docUuid, "Decision: " + decision.getDecision());

            // 5. Routing Engine
            String route = routingService.determineRoute(profile, decision);
            auditService.logSuccess("OCR_ROUTED", "Document", docUuid, "Routed to: " + route);
            
            // 6. Queue State Transition (Simulated Execution Flow)
            queueService.updateQueueState(docUuid, OCRQueueState.QUEUED, "Queued for OCR processing");
            queueService.updateQueueState(docUuid, OCRQueueState.PROCESSING, "Processing with strategy: " + strategyPolicy);
            
            // 7. Validation Engine
            OCRValidationResponse validation = validationService.validateOCRResult(profile);
            auditService.logSuccess("OCR_VALIDATED", "Document", docUuid, "Validation passed: " + validation.getIsValid());
            
            // 8. Governance & Final State
            String finalStatus;
            String message;
            
            if ("MANUAL_REVIEW".equals(decision.getDecision()) || !validation.getIsValid()) {
                finalStatus = "MANUAL_REVIEW";
                message = "OCR result requires manual judicial review.";
                queueService.updateQueueState(docUuid, OCRQueueState.WAITING, message);
                auditService.logFailure("OCR_MANUAL_REVIEW", "Document", docUuid, message);
                notificationService.sendInAppNotification(userUuid, "OCR Manual Review Required", "Document requires manual OCR review.", docUuid, "Document");
                monitoringService.recordFailure(false);
            } else if ("RETRY_OCR".equals(decision.getDecision())) {
                finalStatus = "RETRY";
                message = "OCR quality too low. Scheduled for retry.";
                queueService.updateQueueState(docUuid, OCRQueueState.QUEUED, message);
                auditService.logFailure("OCR_RETRY_REQUIRED", "Document", docUuid, message);
                monitoringService.recordFailure(true);
            } else if ("SKIP_OCR".equals(decision.getDecision()) || "REJECTED".equals(route)) {
                finalStatus = "REJECTED";
                message = "OCR processing skipped or rejected.";
                queueService.updateQueueState(docUuid, OCRQueueState.REJECTED, message);
                auditService.logFailure("OCR_REJECTED", "Document", docUuid, message);
            } else {
                finalStatus = "ACCEPTED";
                message = "OCR process completed successfully.";
                queueService.updateQueueState(docUuid, OCRQueueState.COMPLETED, message);
                auditService.logSuccess("OCR_COMPLETED", "Document", docUuid, message);
                notificationService.sendInAppNotification(userUuid, "OCR Completed", "Document OCR processing finished successfully.", docUuid, "Document");
                monitoringService.recordSuccess(System.currentTimeMillis() - startTime);
            }

            // 9. Telemetry & Dataset Logging
            datasetService.recordDatasetEntry(profile, validation, System.currentTimeMillis() - startTime);

            return OCRWorkflowResponse.builder()
                    .documentUuid(docUuid)
                    .status(finalStatus)
                    .message(message)
                    .profile(profile)
                    .decision(decision)
                    .validation(validation)
                    .build();
                    
        } catch (Exception e) {
            log.error("OCR Workflow failed for document {}", docUuid, e);
            monitoringService.recordFailure(false);
            auditService.logFailure("OCR_FAILED", "Document", docUuid, e.getMessage());
            notificationService.sendInAppNotification(userUuid, "OCR Failed", "A critical error occurred during OCR orchestration.", docUuid, "Document");
            throw new RuntimeException("OCR Workflow Orchestration failed.", e);
        }
    }
}
