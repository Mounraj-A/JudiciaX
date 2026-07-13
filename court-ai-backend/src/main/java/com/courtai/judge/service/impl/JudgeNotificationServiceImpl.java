package com.courtai.judge.service.impl;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.hearing.entity.Hearing;
import com.courtai.judge.entity.JudgeOrder;
import com.courtai.judge.service.JudgeNotificationService;
import com.courtai.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link JudgeNotificationService}.
 * Delegates to the existing {@link NotificationService} (stub in Phase 1).
 *
 * <p>Notifies relevant parties (advocates, clerk, admin) when key judicial
 * events occur: hearing scheduled, hearing adjourned, judgment reserved,
 * case disposed, order uploaded.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JudgeNotificationServiceImpl implements JudgeNotificationService {

    private final NotificationService notificationService;

    @Override
    public void notifyHearingScheduled(CaseFile caseFile, Hearing hearing) {
        String caseUuid    = caseFile.getUuid();
        String caseTitle   = caseFile.getCaseTitle();
        String title       = "Hearing Scheduled — " + caseTitle;
        String message     = "A hearing has been scheduled for case " + caseFile.getCaseNumber()
                + " on " + hearing.getScheduledAt().toLocalDate() + ".";

        notifyAdvocates(caseFile, title, message, caseUuid, "CaseFile");
        notifyClerkIfRegistered(caseFile, title, message, caseUuid, "CaseFile");
        log.info("[JUDGE-NOTIFY] Hearing scheduled — case={}", caseUuid);
    }

    @Override
    public void notifyHearingAdjourned(CaseFile caseFile, Hearing hearing) {
        String caseUuid  = caseFile.getUuid();
        String title     = "Hearing Adjourned — " + caseFile.getCaseTitle();
        String message   = "The hearing for case " + caseFile.getCaseNumber()
                + " has been adjourned. Reason: " + hearing.getAdjournReason()
                + (hearing.getNextHearingDate() != null
                        ? ". Next date: " + hearing.getNextHearingDate() : ".");

        notifyAdvocates(caseFile, title, message, caseUuid, "CaseFile");
        notifyClerkIfRegistered(caseFile, title, message, caseUuid, "CaseFile");
        log.info("[JUDGE-NOTIFY] Hearing adjourned — case={}", caseUuid);
    }

    @Override
    public void notifyJudgmentReserved(CaseFile caseFile) {
        String caseUuid = caseFile.getUuid();
        String title    = "Judgment Reserved — " + caseFile.getCaseTitle();
        String message  = "Judgment has been reserved for case " + caseFile.getCaseNumber() + ".";

        notifyAdvocates(caseFile, title, message, caseUuid, "CaseFile");
        notifyClerkIfRegistered(caseFile, title, message, caseUuid, "CaseFile");
        log.info("[JUDGE-NOTIFY] Judgment reserved — case={}", caseUuid);
    }

    @Override
    public void notifyCaseDisposed(CaseFile caseFile) {
        String caseUuid = caseFile.getUuid();
        String title    = "Case Disposed — " + caseFile.getCaseTitle();
        String message  = "Case " + caseFile.getCaseNumber() + " has been disposed.";

        notifyAdvocates(caseFile, title, message, caseUuid, "CaseFile");
        notifyClerkIfRegistered(caseFile, title, message, caseUuid, "CaseFile");
        log.info("[JUDGE-NOTIFY] Case disposed — case={}", caseUuid);
    }

    @Override
    public void notifyOrderUploaded(CaseFile caseFile, JudgeOrder order) {
        String caseUuid = caseFile.getUuid();
        String title    = order.getOrderType() + " Uploaded — " + caseFile.getCaseTitle();
        String message  = "A " + order.getOrderType() + " titled '" + order.getTitle()
                + "' has been uploaded for case " + caseFile.getCaseNumber() + ".";

        notifyAdvocates(caseFile, title, message, order.getUuid(), "JudgeOrder");
        notifyClerkIfRegistered(caseFile, title, message, order.getUuid(), "JudgeOrder");
        log.info("[JUDGE-NOTIFY] Order uploaded — case={} order={}", caseUuid, order.getUuid());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void notifyAdvocates(CaseFile caseFile, String title, String message,
                                  String refUuid, String refType) {
        if (caseFile.getPetitionerAdvocate() != null) {
            notificationService.sendInAppNotification(
                    caseFile.getPetitionerAdvocate().getUser().getUuid(),
                    title, message, refUuid, refType);
        }
        if (caseFile.getRespondentAdvocate() != null) {
            notificationService.sendInAppNotification(
                    caseFile.getRespondentAdvocate().getUser().getUuid(),
                    title, message, refUuid, refType);
        }
    }

    private void notifyClerkIfRegistered(CaseFile caseFile, String title, String message,
                                          String refUuid, String refType) {
        if (caseFile.getRegisteredByUuid() != null) {
            notificationService.sendInAppNotification(
                    caseFile.getRegisteredByUuid(),
                    title, message, refUuid, refType);
        }
    }
}
