package com.courtai.master.service.impl;

import com.courtai.casecategory.repository.CaseCategoryRepository;
import com.courtai.master.dto.CaseCategoryDto;
import com.courtai.master.dto.CaseTypeDto;
import com.courtai.master.mapper.MasterCaseDataMapper;
import com.courtai.master.repository.CaseTypeRepository;
import com.courtai.master.service.MasterCaseDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MasterCaseDataServiceImpl implements MasterCaseDataService {

    private final CaseTypeRepository caseTypeRepository;
    private final CaseCategoryRepository caseCategoryRepository;
    private final MasterCaseDataMapper masterCaseDataMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CaseTypeDto> getActiveCaseTypes() {
        return caseTypeRepository.findByIsActiveTrueAndIsDeletedFalseOrderByDisplayOrderAsc()
                .stream()
                .map(masterCaseDataMapper::toCaseTypeDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseCategoryDto> getActiveCaseCategories() {
        return caseCategoryRepository.findByIsActiveTrueAndIsDeletedFalseOrderByDisplayOrderAsc()
                .stream()
                .map(masterCaseDataMapper::toCaseCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseCategoryDto> getCaseCategoriesByTypeUuid(String caseTypeUuid) {
        return caseCategoryRepository.findByCaseTypeUuidAndIsActiveTrueAndIsDeletedFalseOrderByDisplayOrderAsc(caseTypeUuid)
                .stream()
                .map(masterCaseDataMapper::toCaseCategoryDto)
                .collect(Collectors.toList());
    }
}
