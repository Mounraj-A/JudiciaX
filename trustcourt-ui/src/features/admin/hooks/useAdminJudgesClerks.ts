// ─── Admin Judge & Clerk Hooks ────────────────────────────────────────────────
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { adminJudgeApi, type JudgeAssignRequest } from '../api/adminJudgeApi'
import { adminClerkApi, type ClerkAssignRequest, type ClerkTransferRequest } from '../api/adminClerkApi'
import { queryKeys } from '@/lib/queryClient'

// ── Judge Queries ──────────────────────────────────────────────────────────────

/** Paginated judge workload list — GET /admin/judges/workloads?page&size */
export function useJudgeWorkloads(page = 0, size = 20) {
  return useQuery({
    queryKey: [...queryKeys.judges.workloads(), page, size],
    queryFn:  () => adminJudgeApi.listJudgeWorkloads(page, size),
    select:   (res) => res.data,
    placeholderData: (prev) => prev,
  })
}

/** Single judge workload — GET /admin/judges/{judgeUserUuid}/workload */
export function useJudgeWorkload(judgeUserUuid: string) {
  return useQuery({
    queryKey: queryKeys.judges.workload(judgeUserUuid),
    queryFn:  () => adminJudgeApi.getJudgeWorkload(judgeUserUuid),
    enabled:  !!judgeUserUuid,
    select:   (res) => res.data,
  })
}

// ── Judge Mutations ────────────────────────────────────────────────────────────

/**
 * Assign a judge to a case.
 * POST /admin/judges/assign  { caseUuid, judgeUserUuid, reason }
 */
export function useAssignJudge() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: JudgeAssignRequest) => adminJudgeApi.assignJudge(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.judges.workloads() }),
  })
}

/**
 * Transfer a judge on a case.
 * PUT /admin/judges/{judgeUserUuid}/transfer?caseUuid=...&reason=...
 */
export function useTransferJudge() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({
      judgeUserUuid,
      caseUuid,
      reason,
    }: {
      judgeUserUuid: string
      caseUuid: string
      reason: string
    }) => adminJudgeApi.transferJudge(judgeUserUuid, caseUuid, reason),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.judges.workloads() }),
  })
}

// ── Clerk Hooks ────────────────────────────────────────────────────────────────

export function useClerkStatistics(clerkUserUuid: string) {
  return useQuery({
    queryKey: queryKeys.clerks.stats(clerkUserUuid),
    queryFn:  () => adminClerkApi.getClerkStatistics(clerkUserUuid),
    enabled:  !!clerkUserUuid,
    select:   (res) => res.data,
  })
}

export function useAssignClerk() {
  return useMutation({
    mutationFn: (data: ClerkAssignRequest) => adminClerkApi.assignClerk(data),
  })
}

export function useTransferClerk() {
  return useMutation({
    mutationFn: ({ uuid, data }: { uuid: string; data: ClerkTransferRequest }) =>
      adminClerkApi.transferClerk(uuid, data),
  })
}
