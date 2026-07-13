package com.courtai.judge.entity;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * Represents a judicial order or judgment uploaded by the presiding judge.
 *
 * <p>Orders are stored as metadata only — actual file bytes reside in cloud storage
 * (S3/GCS). The {@code storagePath} field holds the cloud storage object key.
 * This pattern mirrors {@link com.courtai.document.entity.Document}.</p>
 *
 * <p>Order types: INTERIM_ORDER, FINAL_ORDER, JUDGMENT</p>
 */
@Entity
@Table(
        name = "judge_orders",
        indexes = {
                @Index(name = "idx_jorder_case_id",    columnList = "case_id"),
                @Index(name = "idx_jorder_judge_id",   columnList = "judge_id"),
                @Index(name = "idx_jorder_type",       columnList = "order_type"),
                @Index(name = "idx_jorder_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JudgeOrder extends BaseEntity {

    // ── Core References ───────────────────────────────────────────────────

    /** The case this order is issued for. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    /** The judge who issued this order. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "judge_id", nullable = false)
    private Judge judge;

    // ── Order Classification ──────────────────────────────────────────────

    /**
     * Type of judicial order.
     * Values: INTERIM_ORDER, FINAL_ORDER, JUDGMENT
     */
    @NotBlank
    @Column(name = "order_type", nullable = false, length = 30)
    private String orderType;

    /** Descriptive title of this order — e.g., "Stay Order dated 12-Jul-2026". */
    @NotBlank
    @Column(name = "title", nullable = false, length = 300)
    private String title;

    /** Full text body of the order or a brief summary. */
    @Column(name = "order_text", columnDefinition = "TEXT")
    private String orderText;

    // ── File Metadata ─────────────────────────────────────────────────────

    /** Original filename as provided at upload. */
    @Column(name = "original_file_name", length = 500)
    private String originalFileName;

    /** MIME type of the uploaded file (e.g., "application/pdf"). */
    @Column(name = "mime_type", length = 100)
    private String mimeType;

    /** Cloud storage object key (S3/GCS path). */
    @Column(name = "storage_path", length = 1000)
    private String storagePath;

    /** Size of the uploaded file in bytes. */
    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    // ── Order Dates ───────────────────────────────────────────────────────

    /** The date on which this order was formally issued. */
    @Column(name = "order_date")
    private LocalDate orderDate;

    // ── Status ────────────────────────────────────────────────────────────

    /**
     * Whether the order has been formally signed and sealed.
     * Unsigned drafts may be edited; signed orders are immutable.
     */
    @Column(name = "is_signed", nullable = false)
    @Builder.Default
    private Boolean isSigned = Boolean.FALSE;

    /** Remarks from the judge when issuing this order. */
    @Column(name = "remarks", length = 1000)
    private String remarks;
}
