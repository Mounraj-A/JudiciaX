package com.courtai.judge.service.impl;

import com.courtai.exception.ResourceNotFoundException;
import com.courtai.judge.entity.Judge;
import com.courtai.judge.repository.JudgeRepository;
import com.courtai.judge.service.JudgeService;
import com.courtai.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link JudgeService}.
 * Resolves the current judge from the security context.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JudgeServiceImpl implements JudgeService {

    private final JudgeRepository judgeRepository;

    @Override
    public Judge getCurrentJudge() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        String userUuid = principal.getUserUuid();
        return judgeRepository.findByUserUuidAndIsDeletedFalse(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Judge profile not found for current user"));
    }

    @Override
    public Judge getJudgeByUserUuid(String userUuid) {
        return judgeRepository.findByUserUuidAndIsDeletedFalse(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Judge", "userUuid", userUuid));
    }
}
