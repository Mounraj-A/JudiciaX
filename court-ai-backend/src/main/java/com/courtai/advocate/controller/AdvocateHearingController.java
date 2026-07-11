package com.courtai.advocate.controller;

import com.courtai.advocate.dto.HearingResponse;
import com.courtai.advocate.mapper.HearingMapper;
import com.courtai.advocate.util.AdvocateSecurityUtil;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.common.dto.ApiResponse;
import com.courtai.exception.UnauthorizedActionException;
import com.courtai.hearing.entity.Hearing;
import com.courtai.hearing.repository.HearingRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for advocate hearing schedule (read-only).
 */
@RestController
@RequestMapping("/advocate/hearings")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADVOCATE')")
@Tag(name = "Advocate Module")
public class AdvocateHearingController {

    private final AdvocateSecurityUtil  securityUtil;
    private final CaseFileRepository    caseFileRepository;
    private final HearingRepository     hearingRepository;
    private final HearingMapper         hearingMapper;

    @GetMapping
    @Operation(summary = "List all hearings for my cases")
    public ResponseEntity<ApiResponse<Page<HearingResponse>>> getMyHearings(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        var advocate = securityUtil.getCurrentAdvocate();

        // Get advocate's cases then load hearings
        var cases = caseFileRepository.findByAdvocateUuid(
                advocate.getUuid(),
                PageRequest.of(0, 1000, Sort.by("createdAt")));

        List<Long> caseIds = cases.getContent().stream()
                .map(c -> c.getId())
                .toList();

        List<Hearing> hearings = hearingRepository.findByCaseFileIdInAndIsDeletedFalseOrderByScheduledAtDesc(caseIds);
        List<HearingResponse> responses = hearings.stream().map(hearingMapper::toHearingResponse).toList();

        int start = page * size;
        int end   = Math.min(start + size, responses.size());
        List<HearingResponse> pageContent = start >= responses.size() ? List.of() : responses.subList(start, end);

        return ResponseEntity.ok(ApiResponse.success("Hearings retrieved",
                new PageImpl<>(pageContent, PageRequest.of(page, size), responses.size())));
    }

    @GetMapping("/{hearingUuid}")
    @Operation(summary = "Get a hearing detail")
    public ResponseEntity<ApiResponse<HearingResponse>> getHearing(
            @PathVariable String hearingUuid) {
        var advocate = securityUtil.getCurrentAdvocate();

        Hearing hearing = hearingRepository.findByUuidAndIsDeletedFalse(hearingUuid)
                .orElseThrow(() -> new com.courtai.exception.ResourceNotFoundException("Hearing", "uuid", hearingUuid));

        // Ownership: hearing must belong to one of advocate's cases
        boolean owns = caseFileRepository.existsByUuidAndAdvocateUuid(
                hearing.getCaseFile().getUuid(), advocate.getUuid());
        if (!owns) {
            throw new UnauthorizedActionException("This hearing does not belong to your cases.");
        }

        return ResponseEntity.ok(ApiResponse.success("Hearing retrieved", hearingMapper.toHearingResponse(hearing)));
    }
}
