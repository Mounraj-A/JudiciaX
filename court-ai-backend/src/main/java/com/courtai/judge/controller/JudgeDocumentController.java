package com.courtai.judge.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.judge.dto.JudgeDocumentResponse;
import com.courtai.judge.service.JudgeDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Read-only document review controller for the judge portal.
 * The judge cannot create, update, or delete documents.
 */
@RestController
@RequestMapping("/judge/cases/{caseUuid}/documents")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_JUDGE')")
@Tag(name = "Judge Module", description = "Judge Portal — Document Review")
public class JudgeDocumentController {

    private final JudgeDocumentService documentService;

    @GetMapping
    @Operation(summary = "List case documents",
               description = "Returns all documents attached to a case assigned to the current judge.")
    public ResponseEntity<ApiResponse<List<JudgeDocumentResponse>>> getDocuments(
            @PathVariable String caseUuid) {
        return ResponseEntity.ok(ApiResponse.success("Documents retrieved",
                documentService.getDocumentsByCase(caseUuid)));
    }

    @GetMapping("/{documentUuid}")
    @Operation(summary = "Get document detail",
               description = "Returns metadata for a specific document. No binary content exposed.")
    public ResponseEntity<ApiResponse<JudgeDocumentResponse>> getDocument(
            @PathVariable String caseUuid,
            @PathVariable String documentUuid) {
        return ResponseEntity.ok(ApiResponse.success("Document retrieved",
                documentService.getDocumentByUuid(caseUuid, documentUuid)));
    }
}
