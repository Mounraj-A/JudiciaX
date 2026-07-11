package com.courtai.advocate.controller;

import com.courtai.advocate.dto.DocumentResponse;
import com.courtai.advocate.service.AdvocateDocumentService;
import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for advocate document management.
 * Documents are scoped to the advocate's own cases.
 */
@RestController
@RequestMapping("/advocate/cases/{caseUuid}/documents")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADVOCATE')")
@Tag(name = "Advocate Module")
public class AdvocateDocumentController {

    private final AdvocateDocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a document for a case")
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(
            @PathVariable String caseUuid,
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "documentType", required = false) String documentType,
            @RequestPart(value = "description",  required = false) String description) {
        DocumentResponse response = documentService.uploadDocument(caseUuid, file, documentType, description);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Document uploaded", response));
    }

    @GetMapping
    @Operation(summary = "List documents for a case")
    public ResponseEntity<ApiResponse<Page<DocumentResponse>>> getDocuments(
            @PathVariable String caseUuid,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success("Documents retrieved",
                documentService.getDocuments(caseUuid, pageable)));
    }

    @GetMapping("/{documentUuid}")
    @Operation(summary = "Get a document's metadata")
    public ResponseEntity<ApiResponse<DocumentResponse>> getDocument(
            @PathVariable String caseUuid,
            @PathVariable String documentUuid) {
        return ResponseEntity.ok(ApiResponse.success("Document retrieved",
                documentService.getDocument(caseUuid, documentUuid)));
    }

    @DeleteMapping("/{documentUuid}")
    @Operation(summary = "Delete a document (only before court verification)")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable String caseUuid,
            @PathVariable String documentUuid) {
        documentService.deleteDocument(caseUuid, documentUuid);
        return ResponseEntity.ok(ApiResponse.success("Document deleted"));
    }
}
