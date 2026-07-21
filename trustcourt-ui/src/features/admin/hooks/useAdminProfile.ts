// ─── Admin Profile Hooks ──────────────────────────────────────────────────────
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { adminProfileApi, type UpdateProfileRequest, type ChangePasswordRequest } from '../api/adminProfileApi'
import { queryKeys } from '@/lib/queryClient'

export function useMyProfile() {
  return useQuery({
    queryKey: queryKeys.users.me(),
    queryFn:  () => adminProfileApi.getMyProfile(),
    select:   (res) => res.data,
  })
}

export function useUpdateMyProfile() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: UpdateProfileRequest) => adminProfileApi.updateMyProfile(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.users.me() }),
  })
}

export function useChangePassword() {
  return useMutation({
    mutationFn: (data: ChangePasswordRequest) => adminProfileApi.changePassword(data),
  })
}

export function useMySessions() {
  return useQuery({
    queryKey: queryKeys.users.sessions(),
    queryFn:  () => adminProfileApi.getMySessions(),
    select:   (res) => res.data,
  })
}

export function useRevokeMySession() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (sessionUuid: string) => adminProfileApi.revokeMySession(sessionUuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.users.sessions() }),
  })
}

export function useRevokeAllMySessions() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: () => adminProfileApi.revokeAllMySessions(),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.users.sessions() }),
  })
}
