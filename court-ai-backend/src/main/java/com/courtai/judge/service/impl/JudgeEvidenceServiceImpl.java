package com.courtai.judge.service.impl;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.evidence.entity.Evidence;
import com.courtai.evidence.repository.EvidenceRepository;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import com.courtai.judge.dto.JudgeEvidenceResponse;
import com.courtai.judge.entity.Judge;
import com.courtai.judge.mapper.JudgeEvidenceMapper;
import com.courtai.judge.service.JudgeEvidenceService;
import com.courtai.judge.service.JudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link JudgeEvidenceService}.
 * Read-only — the judge cannot modify evidence.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JudgeEvidenceServiceImpl implements JudgeEvidenceService {

    private final JudgeService       judgeService;
    private final CaseFileRepository caseFileRepository;
    private final EvidenceRepository evidenceRepository;
    private final JudgeEvidenceMapper evidenceMapper;

    @Override
    public List<JudgeEvidenceResponse> getEvidenceByCase(String caseUuid) {
        Judge    judge    = judgeService.getCurrentJudge();
        getAssignedCase(caseUuid, judge);   // ownership check

        return evidenceRepository.findByCaseFileUuidAndIsDeletedFalse(caseUuid)
                .stream()
                .map(evidenceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JudgeEvidenceResponse getEvidenceByUuid(String caseUuid, String evidenceUuid) {
        Judge    judge    = judgeService.getCurrentJudge();
        CaseFile caseFile = getAssignedCase(caseUuid, judge);

        Evidence evidence = evidenceRepository.findByUuidAndIsDeletedFalse(evidenceUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Evidence", "uuid", evidenceUuid));

        if (!evidence.getCaseFile().getUuid().equals(caseUuid)) {
            throw new UnauthorizedActionException("Evidence does not belong to case: " + caseUuid);
        }

        return evidenceMapper.toResponse(evidence);
    }

    private CaseFile getAssignedCase(String caseUuid, Judge judge) {
        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));
        if (caseFile.getAssignedJudge() == null
                || !judge.getUuid().equals(caseFile.getAssignedJudge().getUuid())) {
            throw new UnauthorizedActionException("Case is not assigned to you: " + caseUuid);
        }
        return caseFile;
    }
}
