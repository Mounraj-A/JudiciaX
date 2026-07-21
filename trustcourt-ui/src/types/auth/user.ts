// ─── User Profile & Identity Types ────────────────────────────────────────────
// Phase F2 – Extended user model beyond the core auth user

import type { UserRole } from './index'

// ─── Court Model ──────────────────────────────────────────────────────────────
export interface CourtModel {
  id:           string
  name:         string
  code:         string
  jurisdiction: string
  district?:    string
  state?:       string
  type:         'HIGH_COURT' | 'DISTRICT_COURT' | 'MAGISTRATE' | 'TRIBUNAL' | 'SUPREME_COURT'
}

// ─── Organization Model ───────────────────────────────────────────────────────
export interface OrganizationModel {
  id:   string
  name: string
  code: string
  type: 'JUDICIARY' | 'LAW_FIRM' | 'GOVERNMENT' | 'PRIVATE'
}

// ─── User Preferences ─────────────────────────────────────────────────────────
export interface UserPreferences {
  language:         string   // e.g. 'en', 'ta', 'hi'
  theme:            'light' | 'dark' | 'system'
  density:          'compact' | 'default' | 'comfortable'
  notifications:    boolean
  dateFormat:       string   // e.g. 'DD/MM/YYYY'
}

// ─── Current User ─────────────────────────────────────────────────────────────
/**
 * Full frontend user model — authoritative representation after backend validation.
 * Mapped from UserProfileResponse by IdentityService.
 *
 * ⚠️  This is NOT derived from decoded JWT claims.
 *     It is always fetched from /api/v1/users/me (Spring Boot).
 */
export interface CurrentUser {
  id:                       string
  fullName:                 string
  email:                    string
  phoneNumber?:             string
  role:                     UserRole
  permissions:              string[]
  avatarUrl?:               string
  isActive:                 boolean
  emailVerified:            boolean
  mobileVerified?:          boolean
  accountStatus:            'ACTIVE' | 'SUSPENDED' | 'PENDING'
  profileCompletionPercent: number
  lastLoginAt?:             string
  createdAt?:               string
}

// ─── User Profile ─────────────────────────────────────────────────────────────
/** Extended profile — includes court, organization, preferences. Stored in userSlice. */
export interface UserProfile extends CurrentUser {
  court?:        CourtModel
  organization?: OrganizationModel
  preferences:   UserPreferences
}
