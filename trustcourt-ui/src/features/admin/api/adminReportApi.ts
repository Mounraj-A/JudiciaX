// ─── Admin Report API ─────────────────────────────────────────────────────────
import { springApi } from '@/api/client/springApiClient'
import type { ApiResponse } from '@/types/api'

export interface CaseStatusDistribution {
  status: string
  count: number
  percentage: number
}

export interface JudgePerformanceScore {
  judgeUuid: string
  fullName: string
  court: string
  disposalRate: number
  avgDisposalDays: number
  totalAssigned: number
  totalDisposed: number
  performanceScore: number
}

export interface CourtReport {
  courtUuid: string
  courtName: string
  totalCases: number
  pendingCases: number
  disposedCases: number
  filingRate: number
  disposalRate: number
}

export interface PerformanceReport {
  period: string
  totalCases: number
  disposedCases: number
  disposalRate: number
  avgDisposalDays: number
  newFilings: number
}

export interface ExportReportRequest {
  reportType: string
  format: 'CSV' | 'EXCEL' | 'PDF'
  startDate?: string
  endDate?: string
  filters?: Record<string, string>
}

export const adminReportApi = {
  getAdminAuditReport: () =>
    springApi.get<ApiResponse<Record<string, unknown>>>('/api/reports/admin/audit'),

  getNotificationReport: () =>
    springApi.get<ApiResponse<Record<string, unknown>>>('/api/reports/admin/notification'),

  getDashboardAnalytics: (role: 'admin' | 'court' | 'judge' | 'advocate' | 'clerk') =>
    springApi.get<ApiResponse<Record<string, unknown>>>(`/api/reports/dashboard/${role}`),

  getCaseStatusDistribution: () =>
    springApi.get<ApiResponse<CaseStatusDistribution[]>>('/api/reports/cases/status-distribution'),

  getCaseBacklog: () =>
    springApi.get<ApiResponse<Record<string, unknown>>>('/api/reports/cases/backlog'),

  getCasePriority: () =>
    springApi.get<ApiResponse<Record<string, unknown>>>('/api/reports/cases/priority'),

  getCaseTrustScore: () =>
    springApi.get<ApiResponse<Record<string, unknown>>>('/api/reports/cases/trust'),

  getJudgeReports: () =>
    springApi.get<ApiResponse<Record<string, unknown>[]>>('/api/reports/judges'),

  getJudgePerformanceScores: () =>
    springApi.get<ApiResponse<JudgePerformanceScore[]>>('/api/reports/judges/performance-scores'),

  getCourtReports: () =>
    springApi.get<ApiResponse<CourtReport[]>>('/api/reports/courts'),

  getCourtGlobalStatus: () =>
    springApi.get<ApiResponse<Record<string, unknown>>>('/api/reports/courts/global/status-distribution'),

  getPerformanceMonthly: () =>
    springApi.get<ApiResponse<PerformanceReport[]>>('/api/reports/performance/monthly'),

  getPerformanceQuarterly: () =>
    springApi.get<ApiResponse<PerformanceReport[]>>('/api/reports/performance/quarterly'),

  getPerformanceAnnual: () =>
    springApi.get<ApiResponse<PerformanceReport[]>>('/api/reports/performance/annual'),

  getAIAnalytics: () =>
    springApi.get<ApiResponse<Record<string, unknown>>>('/api/reports/ai'),

  getResearchAnalytics: () =>
    springApi.get<ApiResponse<Record<string, unknown>>>('/api/reports/research/summary'),

  exportReport: (body: ExportReportRequest) =>
    springApi.post<Blob>('/api/reports/export', body, { responseType: 'blob' }),
}
