package com.courtai.clerk.service;

import com.courtai.clerk.dto.ClerkProfileResponse;

/** Service contract for clerk profile operations. */
public interface ClerkService {

    /** Returns the profile of the currently authenticated clerk. */
    ClerkProfileResponse getMyProfile();
}
