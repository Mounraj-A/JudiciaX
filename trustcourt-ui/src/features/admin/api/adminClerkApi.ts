// ─── Admin Clerk API ──────────────────────────────────────────────────────────
import { springApi } from '@/api/client/springApiClient'
import type { ApiResponse } from '@/types/api'

export interface ClerkStatisticsResponse {
  clerkUuid: string
  fullName: string
  email: string
  courtName: string
  courtUuid: string
  totalFilingsProcessed: number
  pendingFilings: number
  returnedFilings: number
  rejectedFilings: number
  avgProcessingTimeHours: number
  duplicateDetections: number
}

export interface ClerkAssignRequest {
  clerkUserUuid: string
  courtUuid: string
}

export interface ClerkTransferRequest {
  targetCourtUuid: string
  reason: string
}

export const adminClerkApi = {
  assignClerk: (data: ClerkAssignRequest) =>
    springApi.post<ApiResponse<void>>('/admin/clerks/assign', data),

  transferClerk: (clerkUserUuid: string, data: ClerkTransferRequest) =>
    springApi.put<ApiResponse<void>>(`/admin/clerks/${clerkUserUuid}/transfer`, data),

  getClerkStatistics: (clerkUserUuid: string) =>
    springApi.get<ApiResponse<ClerkStatisticsResponse>>(`/admin/clerks/${clerkUserUuid}/statistics`),
}
