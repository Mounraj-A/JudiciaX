// ─── useProfileQuery ──────────────────────────────────────────────────────────
// Phase F2 – Extended profile with court + org. Syncs to userSlice.
import { useEffect }            from 'react'
import { useQuery }             from '@tanstack/react-query'
import { useAppDispatch, useAppSelector } from '@/store'
import { selectIsAuthenticated }          from '@/store/slices/authSlice'
import { profileService }       from '@/features/auth/services/profileService'
import { APP_CONSTANTS }        from '@/constants'

export function useProfileQuery() {
  const dispatch        = useAppDispatch()
  const isAuthenticated = useAppSelector(selectIsAuthenticated)

  useEffect(() => { profileService.init(dispatch) }, [dispatch])

  const query = useQuery({
    queryKey:  ['auth', 'profile'] as const,
    queryFn:   () => profileService.fetchProfile(),
    enabled:   isAuthenticated,
    staleTime: APP_CONSTANTS.QUERY_STALE,
  })

  return query
}
