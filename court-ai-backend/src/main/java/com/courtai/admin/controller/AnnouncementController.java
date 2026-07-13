package com.courtai.admin.controller;

import com.courtai.admin.dto.AnnouncementRequest;
import com.courtai.admin.dto.AnnouncementResponse;
import com.courtai.admin.service.AnnouncementService;
import com.courtai.common.dto.ApiResponse;
import com.courtai.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Announcement controller — full lifecycle management.
 * <p>Base path: {@code /api/v1/admin/announcements}</p>
 */
@RestController
@RequestMapping("/admin/announcements")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin Module — Announcements", description = "Broadcast system announcements to roles")
@SecurityRequirement(name = "bearerAuth")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    @Operation(summary = "List all announcements (paginated)")
    public ResponseEntity<ApiResponse<Page<AnnouncementResponse>>> getAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success("Announcements retrieved",
                announcementService.getAll(PageRequest.of(page, size,
                        Sort.by("createdAt").descending()))));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get announcement by UUID")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> getByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success("Announcement retrieved",
                announcementService.getByUuid(uuid)));
    }

    @PostMapping
    @Operation(summary = "Create a new announcement",
               description = "Validates unique title. Announcement starts inactive by default.")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> create(
            @Valid @RequestBody AnnouncementRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Announcement created",
                        announcementService.create(request, admin.getUserUuid())));
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update an announcement")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> update(
            @PathVariable String uuid,
            @Valid @RequestBody AnnouncementRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        return ResponseEntity.ok(ApiResponse.success("Announcement updated",
                announcementService.update(uuid, request, admin.getUserUuid())));
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete an announcement")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String uuid,
            @AuthenticationPrincipal UserPrincipal admin) {
        announcementService.delete(uuid, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("Announcement deleted"));
    }

    @PutMapping("/{uuid}/publish")
    @Operation(summary = "Publish an announcement",
               description = "Sets isActive=true and assigns startDate=today if not already set.")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> publish(
            @PathVariable String uuid,
            @AuthenticationPrincipal UserPrincipal admin) {
        return ResponseEntity.ok(ApiResponse.success("Announcement published",
                announcementService.publish(uuid, admin.getUserUuid())));
    }

    @PutMapping("/{uuid}/archive")
    @Operation(summary = "Archive an announcement",
               description = "Sets isActive=false and endDate=yesterday.")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> archive(
            @PathVariable String uuid,
            @AuthenticationPrincipal UserPrincipal admin) {
        return ResponseEntity.ok(ApiResponse.success("Announcement archived",
                announcementService.archive(uuid, admin.getUserUuid())));
    }
}
