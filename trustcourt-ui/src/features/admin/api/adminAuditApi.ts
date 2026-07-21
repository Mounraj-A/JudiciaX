// ─── Admin Audit API ──────────────────────────────────────────────────────────
import { springApi } from '@/api/client/springApiClient'
import type { ApiResponse } from '@/types/api'

export interface AuditLogResponse {
  uuid: string
  actorUuid: string
  actorUsername: string
  action: string
  entityType: string
  entityUuid: string
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'
  details: Record<string, unknown>
  ipAddress: string
  correlationId: string
  createdAt: string
}

export interface AuditSearchRequest {
  actorUuid?: string
  action?: string
  entityType?: string
  severity?: string
  startDate?: string
  endDate?: string
  page?: number
  size?: number
}

export interface ComplianceViolation {
  uuid: string
  ruleCode: string
  description: string
  severity: string
  detectedAt: string
}

export interface ExportAuditRequest {
  format: 'CSV' | 'EXCEL' | 'JSON'
  startDate?: string
  endDate?: string
  severity?: string
}

export const adminAuditApi = {
  // Admin package audit (simple paginated list)
  getAdminAuditLogs: (page = 0, size = 20) =>
    springApi.get<ApiResponse<{ content: AuditLogResponse[]; totalElements: number }>>('/admin/audit', {
      params: { page, size },
    }),

  exportAdminAudit: () =>
    springApi.get<Blob>('/admin/audit/export', { responseType: 'blob' }),

  // Audit package (full capability)
  getAuditLogById: (uuid: string) =>
    springApi.get<ApiResponse<AuditLogResponse>>(`/api/v1/audit/${uuid}`),

  getAuditTimeline: (correlationId: string) =>
    springApi.get<ApiResponse<AuditLogResponse[]>>(`/api/v1/audit/timeline/${correlationId}`),

  searchAuditLogs: (body: AuditSearchRequest) =>
    springApi.post<ApiResponse<{ content: AuditLogResponse[]; totalElements: number }>>('/api/v1/audit/search', body),

  exportAuditLogs: (body: ExportAuditRequest) =>
    springApi.post<Blob>('/api/v1/audit/export', body, { responseType: 'blob' }),

  getComplianceViolations: () =>
    springApi.get<ApiResponse<ComplianceViolation[]>>('/api/v1/audit/compliance/violations'),

  verifyIntegrity: (uuid: string) =>
    springApi.get<ApiResponse<{ valid: boolean; message: string }>>(`/api/v1/audit/integrity/verify/${uuid}`),

  verifyChain: () =>
    springApi.get<ApiResponse<{ valid: boolean; message: string }>>('/api/v1/audit/integrity/verify-chain'),

  getSecurityAuditEvents: () =>
    springApi.get<ApiResponse<AuditLogResponse[]>>('/api/v1/audit/security/events'),
}
