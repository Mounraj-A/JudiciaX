package com.courtai.advocate.service.impl;

import com.courtai.advocate.dto.AdvocateProfileResponse;
import com.courtai.advocate.dto.UpdateAdvocateProfileRequest;
import com.courtai.advocate.entity.Advocate;
import com.courtai.advocate.mapper.AdvocateMapper;
import com.courtai.advocate.repository.AdvocateRepository;
import com.courtai.advocate.service.AdvocateService;
import com.courtai.advocate.util.AdvocateSecurityUtil;
import com.courtai.audit.service.AuditService;
import com.courtai.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link AdvocateService} — advocate profile management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdvocateServiceImpl implements AdvocateService {

    private final AdvocateSecurityUtil  securityUtil;
    private final AdvocateRepository    advocateRepository;
    private final AdvocateMapper        advocateMapper;
    private final AuditService          auditService;

    @Override
    @Transactional(readOnly = true)
    public AdvocateProfileResponse getMyProfile() {
        Advocate advocate = securityUtil.getCurrentAdvocate();
        return advocateMapper.toProfileResponse(advocate);
    }

    @Override
    @Transactional
    public AdvocateProfileResponse updateMyProfile(UpdateAdvocateProfileRequest request) {
        Advocate advocate = securityUtil.getCurrentAdvocate();

        // Bar council number uniqueness check (if being changed)
        if (request.getBarCouncilNumber() != null
                && !request.getBarCouncilNumber().equals(advocate.getBarCouncilNumber())) {
            if (advocateRepository.existsByBarCouncilNumber(request.getBarCouncilNumber())) {
                throw new DuplicateResourceException(
                        "Bar Council Number '" + request.getBarCouncilNumber() + "' is already registered.");
            }
            advocate.setBarCouncilNumber(request.getBarCouncilNumber());
        }

        // Apply non-null fields
        if (request.getEnrollmentDate()  != null) advocate.setEnrollmentDate(request.getEnrollmentDate());
        if (request.getStateBarCouncil() != null) advocate.setStateBarCouncil(request.getStateBarCouncil());
        if (request.getLawFirm()         != null) advocate.setLawFirm(request.getLawFirm());
        if (request.getSpecialization()  != null) advocate.setSpecialization(request.getSpecialization());
        if (request.getYearsOfPractice() != null) advocate.setYearsOfPractice(request.getYearsOfPractice());
        if (request.getOfficeAddress()   != null) advocate.setOfficeAddress(request.getOfficeAddress());
        if (request.getOfficeCity()      != null) advocate.setOfficeCity(request.getOfficeCity());
        if (request.getOfficeState()     != null) advocate.setOfficeState(request.getOfficeState());
        if (request.getOfficePincode()   != null) advocate.setOfficePincode(request.getOfficePincode());

        Advocate saved = advocateRepository.save(advocate);

        auditService.logSuccess(
                "ADVOCATE_PROFILE_UPDATED",
                "Advocate",
                saved.getUuid(),
                "Advocate profile updated by " + saved.getUser().getEmail());

        log.info("Advocate profile updated: {}", saved.getUuid());
        return advocateMapper.toProfileResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Advocate getCurrentAdvocateEntity() {
        return securityUtil.getCurrentAdvocate();
    }
}
