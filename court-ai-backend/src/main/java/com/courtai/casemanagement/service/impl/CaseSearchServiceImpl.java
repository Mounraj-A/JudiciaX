package com.courtai.casemanagement.service.impl;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.casemanagement.dto.CaseSearchRequest;
import com.courtai.casemanagement.dto.CaseSummaryResponse;
import com.courtai.casemanagement.mapper.CaseMapper;
import com.courtai.casemanagement.service.CaseSearchService;
import com.courtai.common.enums.CasePriority;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaseSearchServiceImpl implements CaseSearchService {

    private final CaseFileRepository caseFileRepository;
    private final CaseMapper         caseMapper;

    @Override
    public Page<CaseSummaryResponse> search(CaseSearchRequest req) {
        Sort sort = "asc".equalsIgnoreCase(req.getSortDirection())
                ? Sort.by(req.getSortBy()).ascending()
                : Sort.by(req.getSortBy()).descending();
        PageRequest pageable = PageRequest.of(req.getPage(), req.getSize(), sort);

        Specification<CaseFile> spec = buildSpec(req);
        return caseFileRepository.findAll(spec, pageable).map(caseMapper::toSummary);
    }

    // ── Specification Builder ─────────────────────────────────────────────────

    private Specification<CaseFile> buildSpec(CaseSearchRequest req) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Soft-delete guard — always applied
            predicates.add(cb.isFalse(root.get("isDeleted")));

            // ── Identifiers ──────────────────────────────────────────────────
            if (notBlank(req.getCaseNumber()))
                predicates.add(cb.like(cb.lower(root.get("caseNumber")),
                        like(req.getCaseNumber())));

            if (notBlank(req.getCaseUuid()))
                predicates.add(cb.equal(root.get("uuid"), req.getCaseUuid()));

            if (notBlank(req.getCnrNumber()))
                predicates.add(cb.equal(root.get("cnrNumber"), req.getCnrNumber()));

            if (notBlank(req.getOfficialCaseNumber()))
                predicates.add(cb.equal(root.get("officialCaseNumber"), req.getOfficialCaseNumber()));

            // ── Parties ──────────────────────────────────────────────────────
            if (notBlank(req.getPetitionerName()))
                predicates.add(cb.like(cb.lower(root.get("petitionerName")),
                        like(req.getPetitionerName())));

            if (notBlank(req.getRespondentName()))
                predicates.add(cb.like(cb.lower(root.get("respondentName")),
                        like(req.getRespondentName())));

            // ── Assignments ──────────────────────────────────────────────────
            if (notBlank(req.getJudgeUuid()))
                predicates.add(cb.equal(
                        root.get("assignedJudge").get("uuid"), req.getJudgeUuid()));

            if (notBlank(req.getAdvocateUuid()))
                predicates.add(cb.or(
                        cb.equal(root.get("petitionerAdvocate").get("uuid"), req.getAdvocateUuid()),
                        cb.equal(root.get("respondentAdvocate").get("uuid"), req.getAdvocateUuid())));

            // ── Court ────────────────────────────────────────────────────────
            if (notBlank(req.getCourtUuid()))
                predicates.add(cb.equal(root.get("court").get("uuid"), req.getCourtUuid()));

            // ── Classification ───────────────────────────────────────────────
            if (req.getCaseType() != null)
                predicates.add(cb.equal(root.get("caseType"), req.getCaseType()));

            if (req.getStatus() != null)
                predicates.add(cb.equal(root.get("status"), req.getStatus()));

            if (req.getPriority() != null)
                predicates.add(cb.equal(root.get("priority"), req.getPriority()));

            // ── High-priority shortcut ───────────────────────────────────────
            if (Boolean.TRUE.equals(req.getHighPriority()))
                predicates.add(root.get("priority").in(
                        CasePriority.HIGH, CasePriority.CRITICAL));

            // ── AI Pending ───────────────────────────────────────────────────
            if (Boolean.TRUE.equals(req.getAiPending()))
                predicates.add(cb.isNull(root.get("priorityScore")));

            if (req.getMinPriorityScore() != null)
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("priorityScore"), req.getMinPriorityScore()));

            // ── Date Range ───────────────────────────────────────────────────
            if (req.getFilingYear() != null)
                predicates.add(cb.equal(root.get("filingYear"), req.getFilingYear()));

            if (req.getFilingDateFrom() != null)
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("filingDate"), req.getFilingDateFrom()));

            if (req.getFilingDateTo() != null)
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("filingDate"), req.getFilingDateTo()));

            // ── Full-text keyword ────────────────────────────────────────────
            if (notBlank(req.getKeyword())) {
                String kw = like(req.getKeyword());
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("caseTitle")),         kw),
                        cb.like(cb.lower(root.get("caseNumber")),        kw),
                        cb.like(cb.lower(root.get("petitionerName")),    kw),
                        cb.like(cb.lower(root.get("respondentName")),    kw),
                        cb.like(cb.lower(root.get("caseDescription")),   kw)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private boolean notBlank(String s) { return s != null && !s.isBlank(); }
    private String like(String value)  { return "%" + value.toLowerCase() + "%"; }
}
