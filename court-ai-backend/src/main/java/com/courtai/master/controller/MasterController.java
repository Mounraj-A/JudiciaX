package com.courtai.master.controller;

import com.courtai.casecategory.repository.CaseCategoryRepository;
import com.courtai.court.repository.CourtRepository;
import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/master")
@RequiredArgsConstructor
@Tag(name = "Master Data API")
public class MasterController {

    private final CourtRepository courtRepository;
    private final CaseCategoryRepository caseCategoryRepository;

    @GetMapping("/courts")
    @Operation(summary = "Get list of all active courts")
    public ResponseEntity<ApiResponse<java.util.List<com.courtai.court.entity.Court>>> getCourts() {
        return ResponseEntity.ok(ApiResponse.success("Courts retrieved", courtRepository.findAll()));
    }

}
