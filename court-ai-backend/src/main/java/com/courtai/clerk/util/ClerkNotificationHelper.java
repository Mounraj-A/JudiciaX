package com.courtai.clerk.util;

import com.courtai.common.enums.NotificationType;
import com.courtai.notification.entity.Notification;
import com.courtai.notification.repository.NotificationRepository;
import com.courtai.user.entity.User;
import com.courtai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Helper for sending advocate notifications from clerk actions.
 *
 * <p>Wraps common notification patterns: case returned, case registered,
 * documents missing, evidence rejected, duplicate warning.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClerkNotificationHelper {

    private final NotificationRepository notificationRepository;
    private final UserRepository         userRepository;

    /** Notify advocate that their case was returned with objections. */
    public void notifyCaseReturned(String advocateUserUuid, String caseUuid, String caseNumber, String reason) {
        send(advocateUserUuid,
             "Case Returned — Action Required",
             "Your case " + caseNumber + " has been returned by the clerk. Reason: " + reason,
             caseUuid,
             "CaseFile");
    }

    /** Notify advocate that their case has been registered. */
    public void notifyCaseRegistered(String advocateUserUuid, String caseUuid,
                                     String officialCaseNumber, String caseNumber) {
        send(advocateUserUuid,
             "Case Registered Successfully",
             "Your case " + caseNumber + " has been officially registered as " + officialCaseNumber + ".",
             caseUuid,
             "CaseFile");
    }

    /** Notify advocate about missing or rejected documents. */
    public void notifyDocumentRejected(String advocateUserUuid, String caseUuid,
                                       String caseNumber, String documentName, String reason) {
        send(advocateUserUuid,
             "Document Rejected — " + documentName,
             "Document '" + documentName + "' for case " + caseNumber + " was rejected. Reason: " + reason,
             caseUuid,
             "Document");
    }

    /** Notify advocate about rejected evidence. */
    public void notifyEvidenceRejected(String advocateUserUuid, String caseUuid,
                                       String caseNumber, String evidenceTitle, String reason) {
        send(advocateUserUuid,
             "Evidence Rejected — " + evidenceTitle,
             "Evidence '" + evidenceTitle + "' for case " + caseNumber + " was rejected. Reason: " + reason,
             caseUuid,
             "Evidence");
    }

    /** Notify advocate about a duplicate case warning. */
    public void notifyDuplicateWarning(String advocateUserUuid, String caseUuid, String caseNumber) {
        send(advocateUserUuid,
             "Duplicate Case Warning",
             "Case " + caseNumber + " may be a duplicate. The clerk is reviewing it.",
             caseUuid,
             "CaseFile");
    }

    // ── Private Helpers ───────────────────────────────────────────────────

    private void send(String recipientUserUuid, String title, String message,
                      String referenceUuid, String referenceType) {
        userRepository.findByUuidAndIsDeletedFalse(recipientUserUuid).ifPresentOrElse(user -> {
            Notification notification = Notification.builder()
                    .recipient(user)
                    .notificationType(NotificationType.IN_APP)
                    .title(title)
                    .message(message)
                    .referenceUuid(referenceUuid)
                    .referenceType(referenceType)
                    .isRead(Boolean.FALSE)
                    .isSent(Boolean.FALSE)
                    .build();
            notificationRepository.save(notification);
            log.info("Notification sent to user {} — {}", recipientUserUuid, title);
        }, () -> log.warn("Cannot send notification — user not found: {}", recipientUserUuid));
    }
}
