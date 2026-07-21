// ─── Identity Context ─────────────────────────────────────────────────────────
// Phase F2 – Extended user identity: profile, court, org, preferences
// Reads from Redux userSlice — owns no state.
import React, { createContext, useContext } from 'react'
import { useAppSelector } from '@/store'
import {
  selectProfile, selectCourt, selectOrganization,
  selectPreferences, selectIsProfileLoading,
} from '@/store/slices/userSlice'
import type { UserProfile, CourtModel, OrganizationModel, UserPreferences } from '@/types/auth/user'

export interface IdentityContextValue {
  profile:          UserProfile | null
  court:            CourtModel | null
  organization:     OrganizationModel | null
  preferences:      UserPreferences | null
  isProfileLoading: boolean
}

const IdentityContext = createContext<IdentityContextValue | null>(null)

export function IdentityProvider({ children }: { children: React.ReactNode }) {
  const profile          = useAppSelector(selectProfile)
  const court            = useAppSelector(selectCourt)
  const organization     = useAppSelector(selectOrganization)
  const preferences      = useAppSelector(selectPreferences)
  const isProfileLoading = useAppSelector(selectIsProfileLoading)

  return (
    <IdentityContext.Provider value={{ profile, court, organization, preferences, isProfileLoading }}>
      {children}
    </IdentityContext.Provider>
  )
}

export function useIdentityContext(): IdentityContextValue {
  const ctx = useContext(IdentityContext)
  if (!ctx) throw new Error('useIdentityContext must be used within <IdentityProvider>')
  return ctx
}
