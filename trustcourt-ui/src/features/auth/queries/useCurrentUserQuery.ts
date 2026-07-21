// ─── useCurrentUserQuery ─────────────────────────────────────────────────────
// Phase F2 – Fetches /users/me from Spring Boot and populates userSlice
// TanStack Query v5: onSuccess is removed — use useEffect to sync to Redux.
import { useEffect }           from 'react'
import { useQuery }            from '@tanstack/react-query'
import { useAppDispatch, useAppSelector } from '@/store'
import { selectIsAuthenticated }          from '@/store/slices/authSlice'
import { identityService }     from '@/features/auth/services/identityService'
import { profileService }      from '@/features/auth/services/profileService'
import { APP_CONSTANTS }       from '@/constants'
import { queryKeys }           from '@/lib/queryClient'

export function useCurrentUserQuery() {
  const dispatch        = useAppDispatch()
  const isAuthenticated = useAppSelector(selectIsAuthenticated)

  useEffect(() => {
    profileService.init(dispatch)
  }, [dispatch])

  const query = useQuery({
    queryKey:  queryKeys.auth.user,
    queryFn:   () => identityService.getCurrentUser(),
    enabled:   isAuthenticated,
    staleTime: APP_CONSTANTS.QUERY_STALE,
  })

  // Sync server state to Redux when data arrives (TanStack Query v5 pattern)
  useEffect(() => {
    if (query.data && isAuthenticated) {
      profileService.fetchProfile().catch(() => {})
    }
  }, [query.data, isAuthenticated])

  return query
}
