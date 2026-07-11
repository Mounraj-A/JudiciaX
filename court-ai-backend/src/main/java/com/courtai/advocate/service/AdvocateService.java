package com.courtai.advocate.service;

import com.courtai.advocate.dto.AdvocateProfileResponse;
import com.courtai.advocate.dto.UpdateAdvocateProfileRequest;
import com.courtai.advocate.entity.Advocate;

/**
 * Service contract for advocate profile management.
 */
public interface AdvocateService {

    /**
     * Returns the profile of the currently logged-in advocate.
     */
    AdvocateProfileResponse getMyProfile();

    /**
     * Updates the profile of the currently logged-in advocate.
     */
    AdvocateProfileResponse updateMyProfile(UpdateAdvocateProfileRequest request);

    /**
     * Internal helper — resolves the current advocate entity for use by other services.
     */
    Advocate getCurrentAdvocateEntity();
}
