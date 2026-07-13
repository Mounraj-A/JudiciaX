package com.courtai.casemanagement.mapper;

import com.courtai.casefile.entity.CaseStatusHistory;
import com.courtai.casemanagement.dto.CaseHistoryResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/** Maps {@link CaseStatusHistory} entities to response DTOs. */
@Component
public class HistoryMapper {

    public CaseHistoryResponse toResponse(CaseStatusHistory h) {
        return CaseHistoryResponse.builder()
                .uuid(h.getUuid())
                .fromStatus(h.getFromStatus())
                .fromStatusLabel(h.getFromStatus() != null ? h.getFromStatus().name() : null)
                .toStatus(h.getToStatus())
                .toStatusLabel(h.getToStatus().name())
                .changedByUuid(h.getChangedByUuid())
                .changedByRole(h.getChangedByRole())
                .remarks(h.getRemarks())
                .changedAt(h.getChangedAt())
                .build();
    }

    public List<CaseHistoryResponse> toResponseList(List<CaseStatusHistory> histories) {
        return histories.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
