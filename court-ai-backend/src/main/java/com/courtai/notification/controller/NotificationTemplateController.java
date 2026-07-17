package com.courtai.notification.controller;

import com.courtai.notification.dto.NotificationTemplateRequest;
import com.courtai.notification.dto.NotificationTemplateResponse;
import com.courtai.notification.service.NotificationTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/notification-templates")
@RequiredArgsConstructor
@Tag(name = "Notification Module", description = "Admin endpoints for notification templates")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class NotificationTemplateController {

    private final NotificationTemplateService templateService;

    @PostMapping
    @Operation(summary = "Create a new notification template")
    public ResponseEntity<NotificationTemplateResponse> createTemplate(@Valid @RequestBody NotificationTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templateService.createTemplate(request));
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update an existing notification template")
    public ResponseEntity<NotificationTemplateResponse> updateTemplate(
            @PathVariable String uuid,
            @Valid @RequestBody NotificationTemplateRequest request) {
        return ResponseEntity.ok(templateService.updateTemplate(uuid, request));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get a notification template by UUID")
    public ResponseEntity<NotificationTemplateResponse> getTemplate(@PathVariable String uuid) {
        return ResponseEntity.ok(templateService.getTemplateByUuid(uuid));
    }

    @GetMapping
    @Operation(summary = "Get all notification templates")
    public ResponseEntity<List<NotificationTemplateResponse>> getAllTemplates() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete a notification template")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String uuid) {
        templateService.deleteTemplate(uuid);
        return ResponseEntity.noContent().build();
    }
}