package com.courtai.admin.controller;

import com.courtai.admin.dto.*;
import com.courtai.admin.service.CourtManagementService;
import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.courtai.security.UserPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Court management controller — full CRUD for courts, benches, and courtrooms.
 * <p>Base path: {@code /api/v1/admin/courts}</p>
 */
@RestController
@RequestMapping("/admin/courts")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin Module — Court Management", description = "CRUD for courts, benches, and courtrooms")
@SecurityRequirement(name = "bearerAuth")
public class CourtManagementController {

    private final CourtManagementService courtService;

    // ── Courts ────────────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "List all courts (paginated)")
    public ResponseEntity<ApiResponse<Page<CourtResponse>>> getCourts(
            @RequestParam(defaultValue = "0")          int page,
            @RequestParam(defaultValue = "20")         int size,
            @RequestParam(defaultValue = "courtName")  String sortBy,
            @RequestParam(defaultValue = "asc")        String direction) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(direction), sortBy));
        return ResponseEntity.ok(ApiResponse.success("Courts retrieved",
                courtService.getCourts(pageable)));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get court by UUID")
    public ResponseEntity<ApiResponse<CourtResponse>> getCourt(@PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success("Court retrieved",
                courtService.getCourtByUuid(uuid)));
    }

    @PostMapping
    @Operation(summary = "Create a new court",
               description = "Validates unique court code. Admin only.")
    public ResponseEntity<ApiResponse<CourtResponse>> createCourt(
            @Valid @RequestBody CourtRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Court created",
                        courtService.createCourt(request, admin.getUserUuid())));
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update a court")
    public ResponseEntity<ApiResponse<CourtResponse>> updateCourt(
            @PathVariable String uuid,
            @Valid @RequestBody CourtRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        return ResponseEntity.ok(ApiResponse.success("Court updated",
                courtService.updateCourt(uuid, request, admin.getUserUuid())));
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Soft-delete a court",
               description = "Prevents deletion of active courts.")
    public ResponseEntity<ApiResponse<Void>> deleteCourt(
            @PathVariable String uuid,
            @AuthenticationPrincipal UserPrincipal admin) {
        courtService.deleteCourt(uuid, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("Court deleted"));
    }

    // ── Benches ───────────────────────────────────────────────────────────────

    @GetMapping("/{courtUuid}/benches")
    @Operation(summary = "List benches for a court")
    public ResponseEntity<ApiResponse<List<CourtBenchResponse>>> getBenches(
            @PathVariable String courtUuid) {
        return ResponseEntity.ok(ApiResponse.success("Benches retrieved",
                courtService.getBenchesByCourt(courtUuid)));
    }

    @PostMapping("/benches")
    @Operation(summary = "Create a new bench")
    public ResponseEntity<ApiResponse<CourtBenchResponse>> createBench(
            @Valid @RequestBody CourtBenchRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Bench created",
                        courtService.createBench(request, admin.getUserUuid())));
    }

    @PutMapping("/benches/{benchUuid}")
    @Operation(summary = "Update a bench")
    public ResponseEntity<ApiResponse<CourtBenchResponse>> updateBench(
            @PathVariable String benchUuid,
            @Valid @RequestBody CourtBenchRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        return ResponseEntity.ok(ApiResponse.success("Bench updated",
                courtService.updateBench(benchUuid, request, admin.getUserUuid())));
    }

    @DeleteMapping("/benches/{benchUuid}")
    @Operation(summary = "Delete a bench")
    public ResponseEntity<ApiResponse<Void>> deleteBench(
            @PathVariable String benchUuid,
            @AuthenticationPrincipal UserPrincipal admin) {
        courtService.deleteBench(benchUuid, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("Bench deleted"));
    }

    // ── CourtRooms ────────────────────────────────────────────────────────────

    @GetMapping("/{courtUuid}/rooms")
    @Operation(summary = "List courtrooms for a court")
    public ResponseEntity<ApiResponse<List<CourtRoomResponse>>> getRooms(
            @PathVariable String courtUuid) {
        return ResponseEntity.ok(ApiResponse.success("Rooms retrieved",
                courtService.getRoomsByCourt(courtUuid)));
    }

    @PostMapping("/rooms")
    @Operation(summary = "Create a new courtroom")
    public ResponseEntity<ApiResponse<CourtRoomResponse>> createRoom(
            @Valid @RequestBody CourtRoomRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Room created",
                        courtService.createRoom(request, admin.getUserUuid())));
    }

    @PutMapping("/rooms/{roomUuid}")
    @Operation(summary = "Update a courtroom")
    public ResponseEntity<ApiResponse<CourtRoomResponse>> updateRoom(
            @PathVariable String roomUuid,
            @Valid @RequestBody CourtRoomRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        return ResponseEntity.ok(ApiResponse.success("Room updated",
                courtService.updateRoom(roomUuid, request, admin.getUserUuid())));
    }

    @DeleteMapping("/rooms/{roomUuid}")
    @Operation(summary = "Delete a courtroom")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(
            @PathVariable String roomUuid,
            @AuthenticationPrincipal UserPrincipal admin) {
        courtService.deleteRoom(roomUuid, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("Room deleted"));
    }
}
