package com.courtai.advocate.controller;

import com.courtai.advocate.dto.CaseNoteResponse;
import com.courtai.advocate.dto.CreateCaseNoteRequest;
import com.courtai.advocate.util.AdvocateSecurityUtil;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.common.dto.ApiResponse;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import com.courtai.note.entity.AdvocateCaseNote;
import com.courtai.note.repository.AdvocateCaseNoteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/advocate/cases/{caseUuid}/notes")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADVOCATE')")
@Tag(name = "Advocate Module")
public class AdvocateCaseNoteController {

    private final AdvocateSecurityUtil securityUtil;
    private final CaseFileRepository caseFileRepository;
    private final AdvocateCaseNoteRepository noteRepository;

    @PostMapping
    @Operation(summary = "Create a private note for a case")
    public ResponseEntity<ApiResponse<CaseNoteResponse>> createNote(
            @PathVariable String caseUuid,
            @Valid @RequestBody CreateCaseNoteRequest request) {
        
        var advocate = securityUtil.getCurrentAdvocate();
        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));

        if (!caseFileRepository.existsByUuidAndAdvocateUuid(caseUuid, advocate.getUuid())) {
            throw new UnauthorizedActionException("Case does not belong to your account.");
        }

        AdvocateCaseNote note = AdvocateCaseNote.builder()
                .caseFile(caseFile)
                .advocate(advocate)
                .noteTitle(request.getNoteTitle())
                .noteContent(request.getNoteContent())
                .build();
        
        note = noteRepository.save(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Note created", mapToResponse(note)));
    }

    @GetMapping
    @Operation(summary = "List private notes for a case")
    public ResponseEntity<ApiResponse<Page<CaseNoteResponse>>> getNotes(
            @PathVariable String caseUuid,
            Pageable pageable) {
        
        var advocate = securityUtil.getCurrentAdvocate();
        if (!caseFileRepository.existsByUuidAndAdvocateUuid(caseUuid, advocate.getUuid())) {
            throw new UnauthorizedActionException("Case does not belong to your account.");
        }

        Page<AdvocateCaseNote> notes = noteRepository.findByCaseFileUuidAndAdvocateUuidAndIsDeletedFalseOrderByCreatedAtDesc(caseUuid, advocate.getUuid(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Notes retrieved", notes.map(this::mapToResponse)));
    }

    @DeleteMapping("/{noteUuid}")
    @Operation(summary = "Delete a private note")
    public ResponseEntity<ApiResponse<Void>> deleteNote(
            @PathVariable String caseUuid,
            @PathVariable String noteUuid) {
        
        var advocate = securityUtil.getCurrentAdvocate();
        AdvocateCaseNote note = noteRepository.findByUuidAndAdvocateUuidAndIsDeletedFalse(noteUuid, advocate.getUuid())
                .orElseThrow(() -> new ResourceNotFoundException("AdvocateCaseNote", "uuid", noteUuid));

        note.setIsDeleted(true);
        noteRepository.save(note);
        
        return ResponseEntity.ok(ApiResponse.success("Note deleted"));
    }

    private CaseNoteResponse mapToResponse(AdvocateCaseNote note) {
        return CaseNoteResponse.builder()
                .uuid(note.getUuid())
                .caseUuid(note.getCaseFile().getUuid())
                .noteTitle(note.getNoteTitle())
                .noteContent(note.getNoteContent())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }
}
