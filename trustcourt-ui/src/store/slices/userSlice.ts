// ─── Redux User Slice ─────────────────────────────────────────────────────────
// Phase F2 – Extended user profile state (separate from core auth user in authSlice)
//
// authSlice.user  → core auth user (id, role, permissions) — from login response
// userSlice.profile → full profile (court, org, preferences) — from /users/me
//
// Populated by React Query useCurrentUserQuery onSuccess callbacks.
import { createSlice, type PayloadAction } from '@reduxjs/toolkit'
import type { UserProfile, CourtModel, OrganizationModel, UserPreferences } from '@/types/auth/user'

interface UserState {
  profile:         UserProfile | null
  court:           CourtModel | null
  organization:    OrganizationModel | null
  preferences:     UserPreferences | null
  isProfileLoading: boolean
  profileError:    string | null
}

const initialState: UserState = {
  profile:          null,
  court:            null,
  organization:     null,
  preferences:      null,
  isProfileLoading: false,
  profileError:     null,
}

const defaultPreferences: UserPreferences = {
  language:      'en',
  theme:         'light',
  density:       'default',
  notifications: true,
  dateFormat:    'DD/MM/YYYY',
}

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setProfile(state, action: PayloadAction<UserProfile>) {
      state.profile          = action.payload
      state.court            = action.payload.court ?? null
      state.organization     = action.payload.organization ?? null
      state.preferences      = action.payload.preferences ?? defaultPreferences
      state.profileError     = null
      state.isProfileLoading = false
    },
    setPreferences(state, action: PayloadAction<Partial<UserPreferences>>) {
      if (state.preferences) {
        Object.assign(state.preferences, action.payload)
      } else {
        state.preferences = { ...defaultPreferences, ...action.payload }
      }
    },
    setProfileLoading(state, action: PayloadAction<boolean>) {
      state.isProfileLoading = action.payload
    },
    setProfileError(state, action: PayloadAction<string>) {
      state.profileError     = action.payload
      state.isProfileLoading = false
    },
    clearProfile() {
      return { ...initialState }
    },
  },
})

export const {
  setProfile,
  setPreferences,
  setProfileLoading,
  setProfileError,
  clearProfile,
} = userSlice.actions

export const userReducer = userSlice.reducer

// ─── User Selectors ───────────────────────────────────────────────────────────
import type { RootState } from '../index'

export const selectProfile          = (s: RootState) => s.user.profile
export const selectCourt            = (s: RootState) => s.user.court
export const selectOrganization     = (s: RootState) => s.user.organization
export const selectPreferences      = (s: RootState) => s.user.preferences
export const selectIsProfileLoading = (s: RootState) => s.user.isProfileLoading
export const selectProfileError     = (s: RootState) => s.user.profileError
