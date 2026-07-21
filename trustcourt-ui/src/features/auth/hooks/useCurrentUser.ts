// ─── useCurrentUser Hook ──────────────────────────────────────────────────────
// Phase F2 – Current user: auth identity + extended profile
import { useAuthContext }      from '@/features/auth/contexts/AuthContext'
import { useIdentityContext }  from '@/features/auth/contexts/IdentityContext'
import { useQueryClient }      from '@tanstack/react-query'
import { queryKeys }           from '@/lib/queryClient'

export function useCurrentUser() {
  const { user }                                        = useAuthContext()
  const { profile, court, organization, isProfileLoading } = useIdentityContext()
  const queryClient                                     = useQueryClient()

  const refetch = () => {
    void queryClient.invalidateQueries({ queryKey: queryKeys.auth.user })
  }

  return {
    user,
    profile,
    court,
    organization,
    isProfileLoading,
    refetch,
    // Convenience accessors
    fullName:  profile?.fullName ?? user?.name ?? '',
    avatarUrl: profile?.avatarUrl ?? user?.avatarUrl,
    role:      user?.role,
    email:     user?.email,
  }
}
