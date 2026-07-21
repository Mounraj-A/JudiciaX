package com.courtai.master.service;

import com.courtai.master.dto.CaseCategoryDto;
import com.courtai.master.dto.CaseTypeDto;

import java.util.List;

public interface MasterCaseDataService {
    
    /**
     * Get all active case types for dropdowns and general lookup.
     */
    List<CaseTypeDto> getActiveCaseTypes();

    /**
     * Get all active case categories.
     */
    List<CaseCategoryDto> getActiveCaseCategories();

    /**
     * Get active case categories for a specific case type UUID.
     */
    List<CaseCategoryDto> getCaseCategoriesByTypeUuid(String caseTypeUuid);
}
