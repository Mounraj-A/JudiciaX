package com.courtai.master.controller;

import com.courtai.master.dto.CaseCategoryDto;
import com.courtai.master.dto.CaseTypeDto;
import com.courtai.master.service.MasterCaseDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/master")
@RequiredArgsConstructor
public class MasterCaseDataController {

    private final MasterCaseDataService masterCaseDataService;

    @GetMapping("/case-types")
    public ResponseEntity<List<CaseTypeDto>> getActiveCaseTypes() {
        log.debug("REST request to get active case types");
        return ResponseEntity.ok(masterCaseDataService.getActiveCaseTypes());
    }

    @GetMapping("/case-categories")
    public ResponseEntity<List<CaseCategoryDto>> getActiveCaseCategories() {
        log.debug("REST request to get active case categories");
        return ResponseEntity.ok(masterCaseDataService.getActiveCaseCategories());
    }

    @GetMapping("/case-types/{caseTypeUuid}/categories")
    public ResponseEntity<List<CaseCategoryDto>> getCaseCategoriesByType(@PathVariable String caseTypeUuid) {
        log.debug("REST request to get case categories for type: {}", caseTypeUuid);
        return ResponseEntity.ok(masterCaseDataService.getCaseCategoriesByTypeUuid(caseTypeUuid));
    }
}
