package com.courtai.clerk.controller;

import com.courtai.clerk.dto.DocumentVerificationResponse;
import com.courtai.clerk.dto.VerifyDocumentRequest;
import com.courtai.clerk.service.ClerkDocumentVerificationService;
import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for clerk document verification.
 */
@RestController
@RequestMapping("/clerk/cases/{caseUuid}/documents")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_CLERK')")
@Tag(name = "Clerk Module")
public class ClerkDocumentController {

    private final ClerkDocumentVerificationService documentService;

    @GetMapping
    @Operation(summary = "List all documents for a case")
    public ResponseEntity<ApiResponse<Page<DocumentVerificationResponse>>> getDocuments(
            @PathVariable String caseUuid,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success("Documents retrieved",
                documentService.getDocuments(caseUuid, pageable)));
    }

    @GetMapping("/{documentUuid}")
    @Operation(summary = "Get document detail")
    public ResponseEntity<ApiResponse<DocumentVerificationResponse>> getDocument(
            @PathVariable String caseUuid,
            @PathVariable String documentUuid) {
        return ResponseEntity.ok(ApiResponse.success("Document retrieved",
                documentService.getDocument(caseUuid, documentUuid)));
    }

    @PutMapping("/{documentUuid}/verify")
    @Operation(summary = "Verify or reject a document")
    public ResponseEntity<ApiResponse<DocumentVerificationResponse>> verifyDocument(
            @PathVariable String caseUuid,
            @PathVariable String documentUuid,
            @Valid @RequestBody VerifyDocumentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                request.isApproved() ? "Document verified" : "Document rejected",
                documentService.verifyDocument(caseUuid, documentUuid, request)));
    }
}
