// ─── Admin Workflow API ───────────────────────────────────────────────────────
import { springApi } from '@/api/client/springApiClient'
import type { ApiResponse } from '@/types/api'

export interface WorkflowResponse {
  uuid: string
  workflowType: string
  status: 'RUNNING' | 'QUEUED' | 'COMPLETED' | 'FAILED' | 'CANCELLED'
  entityUuid: string
  entityType: string
  startedAt: string
  completedAt?: string
  errorMessage?: string
  retryCount: number
}

export interface WorkflowStatusSummary {
  running: number
  queued: number
  completed: number
  failed: number
  cancelled: number
}

export const adminWorkflowApi = {
  listWorkflows: (page = 0, size = 20) =>
    springApi.get<ApiResponse<WorkflowResponse[]>>('/api/v1/ai/workflows', {
      params: { page, size },
    }),

  getWorkflowById: (uuid: string) =>
    springApi.get<ApiResponse<WorkflowResponse>>(`/api/v1/ai/workflows/${uuid}`),

  getWorkflowStatus: () =>
    springApi.get<ApiResponse<WorkflowStatusSummary>>('/api/v1/ai/workflows/status'),

  getWorkflowHistory: () =>
    springApi.get<ApiResponse<WorkflowResponse[]>>('/api/v1/ai/workflows/history'),

  retryWorkflow: (uuid: string) =>
    springApi.post<ApiResponse<WorkflowResponse>>('/api/v1/ai/workflows/retry', { uuid }),

  cancelWorkflow: (uuid: string) =>
    springApi.post<ApiResponse<void>>('/api/v1/ai/workflows/cancel', { uuid }),

  restartWorkflow: (uuid: string) =>
    springApi.post<ApiResponse<WorkflowResponse>>('/api/v1/ai/workflows/restart', { uuid }),
}
