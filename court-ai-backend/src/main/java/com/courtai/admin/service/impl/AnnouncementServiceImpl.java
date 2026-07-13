package com.courtai.admin.service.impl;

import com.courtai.admin.dto.AnnouncementRequest;
import com.courtai.admin.dto.AnnouncementResponse;
import com.courtai.admin.entity.SystemAnnouncement;
import com.courtai.admin.mapper.AnnouncementMapper;
import com.courtai.admin.repository.SystemAnnouncementRepository;
import com.courtai.admin.service.AnnouncementService;
import com.courtai.audit.service.AuditService;
import com.courtai.exception.BusinessRuleViolationException;
import com.courtai.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementServiceImpl implements AnnouncementService {

    private final SystemAnnouncementRepository repo;
    private final AnnouncementMapper           mapper;
    private final AuditService                 auditService;

    @Override
    public Page<AnnouncementResponse> getAll(Pageable pageable) {
        return repo.findByIsDeletedFalseOrderByCreatedAtDesc(pageable).map(mapper::toResponse);
    }

    @Override
    public AnnouncementResponse getByUuid(String uuid) {
        return mapper.toResponse(load(uuid));
    }

    @Override
    @Transactional
    public AnnouncementResponse create(AnnouncementRequest req, String adminUuid) {
        if (repo.existsByTitleAndIsDeletedFalse(req.getTitle())) {
            throw new BusinessRuleViolationException(
                    "An announcement with this title already exists.");
        }
        SystemAnnouncement ann = SystemAnnouncement.builder()
                .title(req.getTitle())
                .message(req.getMessage())
                .priority(req.getPriority() != null ? req.getPriority() : "MEDIUM")
                .targetRole(req.getTargetRole() != null ? req.getTargetRole() : "ALL")
                .startDate(req.getStartDate() != null ? req.getStartDate() : LocalDate.now())
                .endDate(req.getEndDate())
                .isActive(req.getIsActive() != null ? req.getIsActive() : Boolean.FALSE)
                .createdByAdmin(adminUuid)
                .build();
        repo.save(ann);
        auditService.logSuccess("ANNOUNCEMENT_CREATED", "SystemAnnouncement", ann.getUuid(),
                "Created by admin " + adminUuid);
        return mapper.toResponse(ann);
    }

    @Override
    @Transactional
    public AnnouncementResponse update(String uuid, AnnouncementRequest req, String adminUuid) {
        SystemAnnouncement ann = load(uuid);
        if (req.getTitle()      != null) ann.setTitle(req.getTitle());
        if (req.getMessage()    != null) ann.setMessage(req.getMessage());
        if (req.getPriority()   != null) ann.setPriority(req.getPriority());
        if (req.getTargetRole() != null) ann.setTargetRole(req.getTargetRole());
        if (req.getStartDate()  != null) ann.setStartDate(req.getStartDate());
        if (req.getEndDate()    != null) ann.setEndDate(req.getEndDate());
        if (req.getIsActive()   != null) ann.setIsActive(req.getIsActive());
        repo.save(ann);
        auditService.logSuccess("ANNOUNCEMENT_UPDATED", "SystemAnnouncement", uuid,
                "Updated by admin " + adminUuid);
        return mapper.toResponse(ann);
    }

    @Override
    @Transactional
    public void delete(String uuid, String adminUuid) {
        SystemAnnouncement ann = load(uuid);
        ann.softDelete();
        repo.save(ann);
        auditService.logSuccess("ANNOUNCEMENT_DELETED", "SystemAnnouncement", uuid,
                "Deleted by admin " + adminUuid);
    }

    @Override
    @Transactional
    public AnnouncementResponse publish(String uuid, String adminUuid) {
        SystemAnnouncement ann = load(uuid);
        ann.setIsActive(Boolean.TRUE);
        if (ann.getStartDate() == null) ann.setStartDate(LocalDate.now());
        repo.save(ann);
        auditService.logSuccess("ANNOUNCEMENT_PUBLISHED", "SystemAnnouncement", uuid,
                "Published by admin " + adminUuid);
        return mapper.toResponse(ann);
    }

    @Override
    @Transactional
    public AnnouncementResponse archive(String uuid, String adminUuid) {
        SystemAnnouncement ann = load(uuid);
        ann.setIsActive(Boolean.FALSE);
        ann.setEndDate(LocalDate.now().minusDays(1));
        repo.save(ann);
        auditService.logSuccess("ANNOUNCEMENT_ARCHIVED", "SystemAnnouncement", uuid,
                "Archived by admin " + adminUuid);
        return mapper.toResponse(ann);
    }

    private SystemAnnouncement load(String uuid) {
        return repo.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("SystemAnnouncement", "uuid", uuid));
    }
}
