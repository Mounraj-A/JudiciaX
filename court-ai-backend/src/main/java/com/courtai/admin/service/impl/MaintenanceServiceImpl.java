package com.courtai.admin.service.impl;

import com.courtai.admin.dto.MaintenanceRequest;
import com.courtai.admin.dto.MaintenanceResponse;
import com.courtai.admin.entity.MaintenanceWindow;
import com.courtai.admin.mapper.MaintenanceMapper;
import com.courtai.admin.repository.MaintenanceWindowRepository;
import com.courtai.admin.service.MaintenanceService;
import com.courtai.audit.service.AuditService;
import com.courtai.exception.BusinessRuleViolationException;
import com.courtai.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceWindowRepository repo;
    private final MaintenanceMapper           mapper;
    private final AuditService                auditService;

    @Override
    public Page<MaintenanceResponse> getAll(Pageable pageable) {
        return repo.findByIsDeletedFalseOrderByStartTimeDesc(pageable).map(mapper::toResponse);
    }

    @Override
    public MaintenanceResponse getByUuid(String uuid) {
        return mapper.toResponse(load(uuid));
    }

    @Override
    @Transactional
    public MaintenanceResponse create(MaintenanceRequest req, String adminUuid) {
        if (req.getEndTime() != null && req.getStartTime() != null
                && !req.getEndTime().isAfter(req.getStartTime())) {
            throw new BusinessRuleViolationException("End time must be after start time.");
        }
        List<MaintenanceWindow> overlapping = repo.findOverlapping(req.getStartTime(), req.getEndTime());
        if (!overlapping.isEmpty()) {
            throw new BusinessRuleViolationException(
                    "A maintenance window already exists in this time range.");
        }
        MaintenanceWindow w = MaintenanceWindow.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .status("SCHEDULED")
                .createdByAdmin(adminUuid)
                .build();
        repo.save(w);
        auditService.logSuccess("MAINTENANCE_CREATED", "MaintenanceWindow", w.getUuid(),
                "Created by admin " + adminUuid);
        return mapper.toResponse(w);
    }

    @Override
    @Transactional
    public MaintenanceResponse update(String uuid, MaintenanceRequest req, String adminUuid) {
        MaintenanceWindow w = load(uuid);
        if ("COMPLETED".equals(w.getStatus()) || "CANCELLED".equals(w.getStatus())) {
            throw new BusinessRuleViolationException("Cannot edit a completed or cancelled maintenance window.");
        }
        if (req.getTitle()       != null) w.setTitle(req.getTitle());
        if (req.getDescription() != null) w.setDescription(req.getDescription());
        if (req.getStartTime()   != null) w.setStartTime(req.getStartTime());
        if (req.getEndTime()     != null) w.setEndTime(req.getEndTime());
        if (req.getStatus()      != null) w.setStatus(req.getStatus());
        repo.save(w);
        auditService.logSuccess("MAINTENANCE_UPDATED", "MaintenanceWindow", uuid,
                "Updated by admin " + adminUuid);
        return mapper.toResponse(w);
    }

    @Override
    @Transactional
    public void delete(String uuid, String adminUuid) {
        MaintenanceWindow w = load(uuid);
        if ("ACTIVE".equals(w.getStatus())) {
            throw new BusinessRuleViolationException("Cannot delete an active maintenance window. Cancel it first.");
        }
        w.softDelete();
        repo.save(w);
        auditService.logSuccess("MAINTENANCE_DELETED", "MaintenanceWindow", uuid,
                "Deleted by admin " + adminUuid);
    }

    @Override @Transactional
    public MaintenanceResponse activate(String uuid, String adminUuid) {
        MaintenanceWindow w = load(uuid);
        w.setStatus("ACTIVE");
        repo.save(w);
        return mapper.toResponse(w);
    }

    @Override @Transactional
    public MaintenanceResponse complete(String uuid, String adminUuid) {
        MaintenanceWindow w = load(uuid);
        w.setStatus("COMPLETED");
        repo.save(w);
        return mapper.toResponse(w);
    }

    @Override @Transactional
    public MaintenanceResponse cancel(String uuid, String adminUuid) {
        MaintenanceWindow w = load(uuid);
        w.setStatus("CANCELLED");
        repo.save(w);
        auditService.logSuccess("MAINTENANCE_CANCELLED", "MaintenanceWindow", uuid,
                "Cancelled by admin " + adminUuid);
        return mapper.toResponse(w);
    }

    private MaintenanceWindow load(String uuid) {
        return repo.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceWindow", "uuid", uuid));
    }
}
