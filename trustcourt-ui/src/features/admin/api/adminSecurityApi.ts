// ─── Admin Security API ───────────────────────────────────────────────────────
import { springApi } from '@/api/client/springApiClient'
import type { ApiResponse } from '@/types/api'

export interface SecurityEventResponse {
  uuid: string
  eventType: string
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'
  actorUuid?: string
  actorUsername?: string
  ipAddress: string
  userAgent?: string
  description: string
  resolved: boolean
  createdAt: string
}

export interface SecuritySummaryResponse {
  failedLoginsToday: number
  activeSessions: number
  highSeverityEvents: number
  suspiciousIps: number
  lockedAccounts: number
  criticalEventsLast24h: number
}

export const adminSecurityApi = {
  getSecurityEvents: (page = 0, size = 20) =>
    springApi.get<ApiResponse<{ content: SecurityEventResponse[]; totalElements: number }>>('/admin/security/events', {
      params: { page, size },
    }),

  getSecuritySummary: () =>
    springApi.get<ApiResponse<SecuritySummaryResponse>>('/admin/security/summary'),

  revokeSession: (sessionUuid: string) =>
    springApi.delete<ApiResponse<void>>(`/admin/security/sessions/${sessionUuid}`),

  revokeUserSessions: (userUuid: string) =>
    springApi.delete<ApiResponse<void>>(`/admin/security/sessions/user/${userUuid}`),
}
