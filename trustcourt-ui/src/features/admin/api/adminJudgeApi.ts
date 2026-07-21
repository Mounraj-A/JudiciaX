// ─── Admin Judge API ──────────────────────────────────────────────────────────
// Connects to: JudgeAdministrationController (/admin/judges)
// All field names match the backend JudgeWorkloadResponse DTO exactly.
import { springApi } from '@/api/client/springApiClient'
import type { ApiResponse } from '@/types/api'
import type { PageResponse } from './adminUserApi'

// ── Response Types ─────────────────────────────────────────────────────────────

/** Matches JudgeWorkloadResponse.java exactly */
export interface JudgeWorkloadResponse {
  judgeUuid: string
  judgeIdNumber: string
  judgeName: string
  designation: string
  courtName: string
  specialization: string
  totalAssignedCases: number
  activeCases: number
  pendingHearings: number
  disposedCases: number
  reservedJudgments: number
}

// ── Request Types ──────────────────────────────────────────────────────────────

/** Matches AssignJudgeRequest.java — caseUuid and judgeUserUuid are @NotBlank */
export interface JudgeAssignRequest {
  caseUuid: string
  judgeUserUuid: string
  reason?: string
}

// ── Workload Computation (client-side only — never sent to backend) ─────────────

export type WorkloadCategory = 'LOW' | 'MEDIUM' | 'HIGH' | 'OVERLOADED'

export function computeWorkloadCategory(total: number): WorkloadCategory {
  if (total <= 20) return 'LOW'
  if (total <= 50) return 'MEDIUM'
  if (total <= 80) return 'HIGH'
  return 'OVERLOADED'
}

export function computeWorkloadScore(total: number): number {
  return Math.min(100, Math.round((total / 100) * 100))
}

// ── API Functions ──────────────────────────────────────────────────────────────
export const adminJudgeApi = {
  /** GET /admin/judges/workloads?page&size — paginated judge workload list */
  listJudgeWorkloads: (page = 0, size = 20) =>
    springApi.get<ApiResponse<PageResponse<JudgeWorkloadResponse>>>('/admin/judges/workloads', {
      params: { page, size },
    }),

  /** GET /admin/judges/{judgeUserUuid}/workload */
  getJudgeWorkload: (judgeUserUuid: string) =>
    springApi.get<ApiResponse<JudgeWorkloadResponse>>(`/admin/judges/${judgeUserUuid}/workload`),

  /**
   * POST /admin/judges/assign
   * Body: { caseUuid, judgeUserUuid, reason }
   */
  assignJudge: (data: JudgeAssignRequest) =>
    springApi.post<ApiResponse<void>>('/admin/judges/assign', data),

  /**
   * PUT /admin/judges/{judgeUserUuid}/transfer
   * Query params: caseUuid, reason  (NOT a request body)
   */
  transferJudge: (judgeUserUuid: string, caseUuid: string, reason: string) =>
    springApi.put<ApiResponse<void>>(
      `/admin/judges/${judgeUserUuid}/transfer`,
      undefined,
      { params: { caseUuid, reason } },
    ),
}
