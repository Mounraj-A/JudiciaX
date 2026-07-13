package com.courtai.judge.service.impl;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.document.entity.Document;
import com.courtai.document.repository.DocumentRepository;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import com.courtai.judge.dto.JudgeDocumentResponse;
import com.courtai.judge.entity.Judge;
import com.courtai.judge.mapper.JudgeDocumentMapper;
import com.courtai.judge.service.JudgeDocumentService;
import com.courtai.judge.service.JudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link JudgeDocumentService}.
 * Read-only — the judge cannot modify documents.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JudgeDocumentServiceImpl implements JudgeDocumentService {

    private final JudgeService       judgeService;
    private final CaseFileRepository caseFileRepository;
    private final DocumentRepository documentRepository;
    private final JudgeDocumentMapper documentMapper;

    @Override
    public List<JudgeDocumentResponse> getDocumentsByCase(String caseUuid) {
        Judge    judge    = judgeService.getCurrentJudge();
        CaseFile caseFile = getAssignedCase(caseUuid, judge);

        return documentRepository.findByCaseFileUuidAndIsDeletedFalse(caseUuid)
                .stream()
                .map(documentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JudgeDocumentResponse getDocumentByUuid(String caseUuid, String documentUuid) {
        Judge    judge    = judgeService.getCurrentJudge();
        CaseFile caseFile = getAssignedCase(caseUuid, judge);

        Document document = documentRepository.findByUuidAndIsDeletedFalse(documentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "uuid", documentUuid));

        // Ensure document belongs to this case
        if (!document.getCaseFile().getUuid().equals(caseUuid)) {
            throw new UnauthorizedActionException("Document does not belong to case: " + caseUuid);
        }

        return documentMapper.toResponse(document);
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
