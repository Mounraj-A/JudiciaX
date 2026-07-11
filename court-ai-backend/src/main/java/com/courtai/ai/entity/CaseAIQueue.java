package com.courtai.ai.entity;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.QueueStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Queue entry for scheduling AI analysis of a judicial case.
 *
 * <p><strong>Placeholder entity — no business logic implemented.</strong>
 * This entity coordinates asynchronous case analysis with the FastAPI AI service.
 * A scheduler will poll for {@code PENDING} entries and dispatch them for processing.</p>
 *
 * <p>On completion, results are written to {@link CaseAnalysis}.</p>
 */
@Entity
@Table(
        name = "case_ai_queue",
        indexes = {
                @Index(name = "idx_aiq_case_id",      columnList = "case_id"),
                @Index(name = "idx_aiq_status",       columnList = "queue_status"),
                @Index(name = "idx_aiq_created_at",   columnList = "created_at"),
                @Index(name = "idx_aiq_is_deleted",   columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseAIQueue extends BaseEntity {

    /** The case to be analysed. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    /** Current processing status of this queue entry. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "queue_status", nullable = false, length = 20)
    @Builder.Default
    private QueueStatus queueStatus = QueueStatus.PENDING;

    /** Number of processing attempts made (for retry logic). */
    @Column(name = "attempts", nullable = false)
    @Builder.Default
    private Integer attempts = 0;

    /** Timestamp when the AI service completed (or failed) processing. */
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    /** Error message if processing failed — populated by the AI service. */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /** JSON snapshot of the case data sent to the AI service for analysis. */
    @Column(name = "request_payload", columnDefinition = "TEXT")
    private String requestPayload;
}
