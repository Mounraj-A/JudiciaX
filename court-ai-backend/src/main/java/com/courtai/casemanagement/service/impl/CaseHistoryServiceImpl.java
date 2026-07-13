package com.courtai.casemanagement.service.impl;

import com.courtai.casefile.repository.CaseAssignmentRepository;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.casefile.repository.CaseStatusHistoryRepository;
import com.courtai.casemanagement.dto.CaseHistoryResponse;
import com.courtai.casemanagement.mapper.HistoryMapper;
import com.courtai.casemanagement.service.CaseHistoryService;
import com.courtai.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaseHistoryServiceImpl implements CaseHistoryService {

    private final CaseFileRepository          caseFileRepository;
    private final CaseStatusHistoryRepository  statusHistoryRepository;
    private final CaseAssignmentRepository     assignmentRepository;
    private final HistoryMapper                historyMapper;

    @Override
    public List<CaseHistoryResponse> getStatusHistory(String caseUuid) {
        verifyCaseExists(caseUuid);
        return historyMapper.toResponseList(
                statusHistoryRepository.findByCaseFileUuidOrderByChangedAtDesc(caseUuid));
    }

    @Override
    public List<CaseHistoryResponse> getAssignmentHistory(String caseUuid) {
        verifyCaseExists(caseUuid);
        // Assignment history uses the status history records filtered to JUDGE_ASSIGNED transitions
        return historyMapper.toResponseList(
                statusHistoryRepository.findByCaseFileUuidOrderByChangedAtDesc(caseUuid)
                        .stream()
                        .filter(h -> com.courtai.common.enums.CaseStatus.JUDGE_ASSIGNED.equals(h.getToStatus()))
                        .toList());
    }

    private void verifyCaseExists(String caseUuid) {
        if (!caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid).isPresent()) {
            throw new ResourceNotFoundException("CaseFile", "uuid", caseUuid);
        }
    }
}
