package com.courtai.judge.service;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.hearing.entity.Hearing;
import com.courtai.judge.entity.JudgeOrder;

/**
 * Service contract for judge-triggered notifications.
 * Delegates to the existing {@link com.courtai.notification.service.NotificationService}.
 */
public interface JudgeNotificationService {

    /** Notifies advocates and clerk when a hearing is scheduled. */
    void notifyHearingScheduled(CaseFile caseFile, Hearing hearing);

    /** Notifies advocates and clerk when a hearing is adjourned. */
    void notifyHearingAdjourned(CaseFile caseFile, Hearing hearing);

    /** Notifies advocates and clerk when a judgment is reserved. */
    void notifyJudgmentReserved(CaseFile caseFile);

    /** Notifies all parties when a case is disposed. */
    void notifyCaseDisposed(CaseFile caseFile);

    /** Notifies advocates and clerk when a judicial order is uploaded. */
    void notifyOrderUploaded(CaseFile caseFile, JudgeOrder order);
}
