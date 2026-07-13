package com.courtai.judge.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.judge.dto.JudgeNoteRequest;
import com.courtai.judge.dto.JudgeNoteResponse;
import com.courtai.judge.service.JudgeNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Private judge notes controller.
 * Notes are strictly private — only the owning judge can access them.
 */
@RestController
@RequestMapping("/judge/cases/{caseUuid}/notes")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_JUDGE')")
@Tag(name = "Judge Module", description = "Judge Portal — Private Notes")
public class JudgeNoteController {

    private final JudgeNoteService noteService;

    @PostMapping
    @Operation(summary = "Create a private note",
               description = "Creates a private judicial note on a case. Only the owning judge can read it.")
    public ResponseEntity<ApiResponse<JudgeNoteResponse>> createNote(
            @PathVariable String caseUuid,
            @Valid @RequestBody JudgeNoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Note created", noteService.createNote(caseUuid, request)));
    }

    @GetMapping
    @Operation(summary = "List my notes for a case",
               description = "Returns all private notes authored by the current judge on this case.")
    public ResponseEntity<ApiResponse<List<JudgeNoteResponse>>> getNotes(
            @PathVariable String caseUuid) {
        return ResponseEntity.ok(ApiResponse.success("Notes retrieved",
                noteService.getNotes(caseUuid)));
    }

    @PutMapping("/{noteUuid}")
    @Operation(summary = "Update a private note")
    public ResponseEntity<ApiResponse<JudgeNoteResponse>> updateNote(
            @PathVariable String caseUuid,
            @PathVariable String noteUuid,
            @Valid @RequestBody JudgeNoteRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Note updated",
                noteService.updateNote(caseUuid, noteUuid, request)));
    }

    @DeleteMapping("/{noteUuid}")
    @Operation(summary = "Delete a private note")
    public ResponseEntity<ApiResponse<Void>> deleteNote(
            @PathVariable String caseUuid,
            @PathVariable String noteUuid) {
        noteService.deleteNote(caseUuid, noteUuid);
        return ResponseEntity.ok(ApiResponse.success("Note deleted"));
    }
}
