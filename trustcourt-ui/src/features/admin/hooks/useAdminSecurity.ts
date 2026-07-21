// ─── Admin Security Hooks ─────────────────────────────────────────────────────
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { adminSecurityApi } from '../api/adminSecurityApi'
import { queryKeys } from '@/lib/queryClient'

export function useSecurityEvents(page = 0, size = 20) {
  return useQuery({
    queryKey: queryKeys.security.events({ page, size }),
    queryFn:  () => adminSecurityApi.getSecurityEvents(page, size),
    refetchInterval: 30_000,
    select:   (res) => res.data,
  })
}

export function useSecuritySummary() {
  return useQuery({
    queryKey: queryKeys.security.summary(),
    queryFn:  () => adminSecurityApi.getSecuritySummary(),
    refetchInterval: 60_000,
    select:   (res) => res.data,
  })
}

export function useRevokeSession() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (sessionUuid: string) => adminSecurityApi.revokeSession(sessionUuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['security'] }),
  })
}

export function useRevokeUserSessions() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (userUuid: string) => adminSecurityApi.revokeUserSessions(userUuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['security'] }),
  })
}
