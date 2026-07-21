// ─── Admin Audit Hooks ────────────────────────────────────────────────────────
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { adminAuditApi, type AuditSearchRequest, type ExportAuditRequest } from '../api/adminAuditApi'
import { queryKeys } from '@/lib/queryClient'

export function useAdminAuditLogs(page = 0, size = 20) {
  return useQuery({
    queryKey: queryKeys.audit.all({ page, size }),
    queryFn:  () => adminAuditApi.getAdminAuditLogs(page, size),
    select:   (res) => res.data,
  })
}

export function useSearchAuditLogs(body: AuditSearchRequest, enabled = true) {
  return useQuery({
    queryKey: ['audit', 'search', body],
    queryFn:  () => adminAuditApi.searchAuditLogs(body),
    enabled,
    select:   (res) => res.data,
  })
}

export function useAuditLogById(uuid: string) {
  return useQuery({
    queryKey: queryKeys.audit.byId(uuid),
    queryFn:  () => adminAuditApi.getAuditLogById(uuid),
    enabled:  !!uuid,
    select:   (res) => res.data,
  })
}

export function useAuditTimeline(correlationId: string) {
  return useQuery({
    queryKey: queryKeys.audit.timeline(correlationId),
    queryFn:  () => adminAuditApi.getAuditTimeline(correlationId),
    enabled:  !!correlationId,
    select:   (res) => res.data,
  })
}

export function useComplianceViolations() {
  return useQuery({
    queryKey: queryKeys.audit.violations(),
    queryFn:  () => adminAuditApi.getComplianceViolations(),
    select:   (res) => res.data,
  })
}

export function useSecurityAuditEvents() {
  return useQuery({
    queryKey: queryKeys.audit.security({}),
    queryFn:  () => adminAuditApi.getSecurityAuditEvents(),
    refetchInterval: 30_000,
    select:   (res) => res.data,
  })
}

export function useVerifyAuditIntegrity() {
  return useMutation({
    mutationFn: (uuid: string) => adminAuditApi.verifyIntegrity(uuid),
  })
}

export function useVerifyAuditChain() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: () => adminAuditApi.verifyChain(),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['audit'] }),
  })
}

export function useExportAuditLogs() {
  return useMutation({
    mutationFn: (body: ExportAuditRequest) => adminAuditApi.exportAuditLogs(body),
  })
}
