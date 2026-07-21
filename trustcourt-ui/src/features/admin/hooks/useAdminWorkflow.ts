// ─── Admin Workflow Hooks ─────────────────────────────────────────────────────
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { adminWorkflowApi } from '../api/adminWorkflowApi'
import { queryKeys } from '@/lib/queryClient'

export function useAdminWorkflows(page = 0, size = 20) {
  return useQuery({
    queryKey: queryKeys.workflows.all({ page, size }),
    queryFn:  () => adminWorkflowApi.listWorkflows(page, size),
    refetchInterval: 10_000, // live monitor — every 10s
    select:   (res) => res.data,
  })
}

export function useWorkflowStatus() {
  return useQuery({
    queryKey: queryKeys.workflows.status(),
    queryFn:  () => adminWorkflowApi.getWorkflowStatus(),
    refetchInterval: 10_000,
    select:   (res) => res.data,
  })
}

export function useWorkflowHistory() {
  return useQuery({
    queryKey: queryKeys.workflows.history(),
    queryFn:  () => adminWorkflowApi.getWorkflowHistory(),
    select:   (res) => res.data,
  })
}

export function useRetryWorkflow() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => adminWorkflowApi.retryWorkflow(uuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['workflows'] }),
  })
}

export function useCancelWorkflow() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => adminWorkflowApi.cancelWorkflow(uuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['workflows'] }),
  })
}

export function useRestartWorkflow() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => adminWorkflowApi.restartWorkflow(uuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['workflows'] }),
  })
}
