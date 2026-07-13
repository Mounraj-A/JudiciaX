package com.courtai.judge.service;

import com.courtai.judge.entity.Judge;

/**
 * Core judge resolution service.
 * Resolves the current judge profile from the authenticated security context.
 */
public interface JudgeService {

    /**
     * Returns the Judge profile for the currently authenticated user.
     * Throws {@link com.courtai.exception.ResourceNotFoundException} if no profile exists.
     */
    Judge getCurrentJudge();

    /**
     * Returns the Judge profile by user UUID.
     * Throws {@link com.courtai.exception.ResourceNotFoundException} if not found.
     */
    Judge getJudgeByUserUuid(String userUuid);
}
