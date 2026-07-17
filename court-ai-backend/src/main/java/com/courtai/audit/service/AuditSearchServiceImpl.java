package com.courtai.audit.service;

import com.courtai.audit.dto.AuditResponse;
import com.courtai.audit.dto.AuditSearchRequest;
import com.courtai.audit.dto.AuditTimelineResponse;
import com.courtai.audit.entity.AuditEvent;
import com.courtai.audit.mapper.AuditMapper;
import com.courtai.audit.repository.AuditEventRepository;
import com.courtai.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditSearchServiceImpl implements AuditSearchService {

    private final AuditEventRepository auditEventRepository;
    private final AuditMapper auditMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<AuditResponse> search(AuditSearchRequest request, Pageable pageable) {
        Specification<AuditEvent> spec = Specification.where((root, query, cb) -> cb.isFalse(root.get("isDeleted")));

        if (request.getModule() != null && !request.getModule().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("module"), request.getModule()));
        }
        if (request.getAction() != null && !request.getAction().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("action"), request.getAction()));
        }
        if (request.getCorrelationId() != null && !request.getCorrelationId().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("correlationId"), request.getCorrelationId()));
        }
        
        Page<AuditEvent> eventPage = auditEventRepository.findAll(spec, pageable);
        return eventPage.map(auditMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditResponse getDetails(String uuid) {
        AuditEvent event = auditEventRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Audit Event not found with uuid: " + uuid));
        return auditMapper.toResponse(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditTimelineResponse> getTimeline(String correlationId) {
        Specification<AuditEvent> spec = Specification.<AuditEvent>where((root, query, cb) -> cb.isFalse(root.get("isDeleted")))
                .and((root, query, cb) -> cb.equal(root.get("correlationId"), correlationId));
        
        List<AuditEvent> events = auditEventRepository.findAll(spec);
        return auditMapper.toTimelineResponseList(events);
    }
}
