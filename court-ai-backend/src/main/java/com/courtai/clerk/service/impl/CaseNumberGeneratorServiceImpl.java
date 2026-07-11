package com.courtai.clerk.service.impl;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.clerk.entity.CaseNumberSequence;
import com.courtai.clerk.repository.CaseNumberSequenceRepository;
import com.courtai.clerk.service.CaseNumberGeneratorService;
import com.courtai.court.entity.Court;
import com.courtai.exception.BusinessRuleViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

/**
 * Implementation of {@link CaseNumberGeneratorService}.
 *
 * <p>Uses a dedicated {@code case_number_sequences} table with a pessimistic
 * row-level lock to guarantee unique case numbers even under concurrent requests.</p>
 *
 * <p>Format: {@code STATE-DISTRICT-COURTCODE-YEAR-SEQUENCE}
 * e.g., {@code TN-COIMBATORE-DC-2026-000001}</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaseNumberGeneratorServiceImpl implements CaseNumberGeneratorService {

    private final CaseNumberSequenceRepository sequenceRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateOfficialCaseNumber(CaseFile caseFile) {
        Court court = caseFile.getCourt();
        if (court == null) {
            throw new BusinessRuleViolationException(
                    "Cannot generate official case number — case has no court assigned.");
        }

        int year = caseFile.getFilingYear() != null
                ? caseFile.getFilingYear()
                : Year.now().getValue();

        // Fetch-or-create sequence row with pessimistic write lock
        CaseNumberSequence seq = sequenceRepository
                .findByCourtIdAndYearForUpdate(court.getId(), year)
                .orElseGet(() -> sequenceRepository.save(
                        CaseNumberSequence.builder()
                                .court(court)
                                .year(year)
                                .lastSeq(0)
                                .build()));

        int nextSeq = seq.getLastSeq() + 1;
        seq.setLastSeq(nextSeq);
        sequenceRepository.save(seq);

        // Build components — normalise to uppercase, replace spaces with hyphens
        String state    = normalise(court.getState(),    "XX");
        String district = normalise(court.getDistrict(), "UNKNOWN");
        String code     = normalise(court.getCourtCode(), "COURT");
        String sequence = String.format("%06d", nextSeq);

        String caseNumber = String.join("-", state, district, code, String.valueOf(year), sequence);
        log.info("Generated official case number: {}", caseNumber);
        return caseNumber;
    }

    private String normalise(String value, String fallback) {
        if (value == null || value.isBlank()) return fallback;
        return value.toUpperCase().replaceAll("\\s+", "-");
    }
}
