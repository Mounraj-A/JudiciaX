// ─── Profile Service ──────────────────────────────────────────────────────────
// Phase F2 – Extended user profile: court, organisation, preferences
import type { AppDispatch } from '@/store'
import type { UserProfile, UserPreferences } from '@/types/auth/user'
import { identityService }  from './identityService'
import { setProfile, setPreferences, setProfileLoading, setProfileError } from '@/store/slices/userSlice'
import { logger }           from '@/core/logger'

class ProfileServiceClass {
  private _dispatch: AppDispatch | null = null

  init(dispatch: AppDispatch): void {
    this._dispatch = dispatch
  }

  private get dispatch(): AppDispatch {
    if (!this._dispatch) throw new Error('[ProfileService] Not initialised')
    return this._dispatch
  }

  /**
   * Fetch profile from /users/me and populate userSlice.
   * Court and organization data is extracted from the profile response.
   */
  async fetchProfile(): Promise<UserProfile> {
    this.dispatch(setProfileLoading(true))
    try {
      const currentUser = await identityService.getCurrentUser()
      const profile: UserProfile = {
        ...currentUser,
        preferences: {
          language:      'en',
          theme:         'light',
          density:       'default',
          notifications: true,
          dateFormat:    'DD/MM/YYYY',
        },
        // court and organization will be populated in future phases
        // when backend exposes these on /users/me
      }
      this.dispatch(setProfile(profile))
      logger.info('[ProfileService] Profile loaded', { role: profile.role })
      return profile
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Failed to load profile'
      this.dispatch(setProfileError(message))
      throw error
    }
  }

  /** Update user preferences in Redux (persisted in Phase F3+). */
  updatePreferences(preferences: Partial<UserPreferences>): void {
    this.dispatch(setPreferences(preferences))
    logger.debug('[ProfileService] Preferences updated', preferences)
  }

  /** Clear profile on logout. */
  clearProfile(): void {
    // Handled by clearProfile action from userSlice directly
  }
}

export const profileService = new ProfileServiceClass()
