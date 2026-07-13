package com.courtai.judge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request payload for uploading a judicial order or judgment.
 * File bytes are NOT sent here — this is metadata only.
 * Actual file upload happens via a separate multipart endpoint or
 * direct cloud storage upload with pre-signed URL.
 */
@Getter
@NoArgsConstructor
public class JudgeOrderRequest {

    /**
     * Type of order being uploaded.
     * Allowed values: INTERIM_ORDER, FINAL_ORDER, JUDGMENT
     */
    @NotBlank(message = "Order type is required")
    private String orderType;

    @NotBlank(message = "Order title is required")
    @Size(max = 300, message = "Title must not exceed 300 characters")
    private String title;

    @Size(max = 10000, message = "Order text must not exceed 10000 characters")
    private String orderText;

    /** Date on which this order was formally issued. */
    private LocalDate orderDate;

    /** Cloud storage path where the order file was uploaded. */
    private String storagePath;

    /** Original filename of the uploaded order document. */
    private String originalFileName;

    /** MIME type of the uploaded file. */
    private String mimeType;

    /** Size of the uploaded file in bytes. */
    private Long fileSizeBytes;

    /** Whether the order has been signed and sealed. */
    private Boolean isSigned;

    @Size(max = 1000, message = "Remarks must not exceed 1000 characters")
    private String remarks;
}
