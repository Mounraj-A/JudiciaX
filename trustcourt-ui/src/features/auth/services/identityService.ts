// ─── Identity Service ─────────────────────────────────────────────────────────
// Phase F2 – Maps backend UserProfileResponse to frontend CurrentUser
//
// ⚠️  SECURITY BOUNDARY: getCurrentUser() ALWAYS calls the Spring Boot backend
//     (/api/v1/users/me). Decoded JWT claims are NEVER used as the profile source.
import { getCurrentUserApi } from '@/api/services/auth/authApi'
import type { ApiUserProfileResponse } from '@/api/services/auth/authApi'
import type { CurrentUser } from '@/types/auth/user'
import type { UserRole } from '@/types/auth'
import { logger } from '@/core/logger'

class IdentityServiceClass {
  /**
   * Fetch the current authenticated user's profile from Spring Boot.
   * This is the authoritative user identity — not derived from JWT.
   */
  async getCurrentUser(): Promise<CurrentUser> {
    logger.debug('[IdentityService] Fetching current user from /users/me')
    const response = await getCurrentUserApi()
    return this.mapToCurrentUser(response.data)
  }

  /**
   * Map backend UserProfileResponse → frontend CurrentUser.
   * Normalises the role field: "ROLE_JUDGE" → "JUDGE"
   */
  mapToCurrentUser(response: ApiUserProfileResponse): CurrentUser {
    // Normalise Spring Security ROLE_ prefix
    const rawRole   = response.role ?? 'VIEWER'
    const role      = rawRole.replace(/^ROLE_/, '') as UserRole

    return {
      id:                       response.uuid,
      fullName:                 response.fullName,
      email:                    response.email,
      phoneNumber:              response.phoneNumber,
      role,
      permissions:              response.permissions ?? [],
      isActive:                 response.accountStatus === 'ACTIVE',
      emailVerified:            response.emailVerified,
      mobileVerified:           response.mobileVerified,
      accountStatus:            response.accountStatus as CurrentUser['accountStatus'],
      profileCompletionPercent: response.profileCompletionPercent ?? 0,
      lastLoginAt:              response.lastLogin,
      createdAt:                response.createdAt,
    }
  }
}

export const identityService = new IdentityServiceClass()
