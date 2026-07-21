import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getMyProfile, updateMyProfile, UpdateAdvocateProfileRequest, AdvocateProfileResponse } from '@/api/services/advocate/advocateApi'
import { AuthError, AuthErrorCode } from '@/features/auth/errors/AuthError'

export const ADVOCATE_PROFILE_QUERY_KEY = ['advocate-profile']

export const useAdvocateProfile = () => {
  return useQuery({
    queryKey: ADVOCATE_PROFILE_QUERY_KEY,
    queryFn: getMyProfile,
    retry: (failureCount, error) => {
      // Don't retry on 401/403
      if (error instanceof AuthError && (error.code === AuthErrorCode.UNAUTHORIZED || error.code === AuthErrorCode.FORBIDDEN)) {
        return false
      }
      return failureCount < 3
    }
  })
}

export const useUpdateAdvocateProfile = () => {
  const queryClient = useQueryClient()

  return useMutation<AdvocateProfileResponse, Error, UpdateAdvocateProfileRequest>({
    mutationFn: updateMyProfile,
    onSuccess: (data) => {
      alert('Profile updated successfully')
      queryClient.setQueryData(ADVOCATE_PROFILE_QUERY_KEY, data)
    },
    onError: (error) => {
      alert(error.message || 'Failed to update profile')
    }
  })
}
