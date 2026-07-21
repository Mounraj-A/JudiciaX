import { useQuery } from '@tanstack/react-query'
import { getDashboard } from '@/api/services/advocate/advocateApi'
import { AuthError, AuthErrorCode } from '@/features/auth/errors/AuthError'

export const ADVOCATE_DASHBOARD_QUERY_KEY = ['advocate-dashboard']

export const useAdvocateDashboard = () => {
  return useQuery({
    queryKey: ADVOCATE_DASHBOARD_QUERY_KEY,
    queryFn: getDashboard,
    retry: (failureCount, error) => {
      if (error instanceof AuthError && (error.code === AuthErrorCode.UNAUTHORIZED || error.code === AuthErrorCode.FORBIDDEN)) {
        return false
      }
      return failureCount < 3
    }
  })
}
