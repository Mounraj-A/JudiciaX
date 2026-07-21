// ─── Admin Report Hooks ───────────────────────────────────────────────────────
import { useQuery, useMutation } from '@tanstack/react-query'
import { adminReportApi, type ExportReportRequest } from '../api/adminReportApi'
import { queryKeys } from '@/lib/queryClient'

export function useAdminDashboardAnalytics(role: 'admin' | 'court' | 'judge' | 'advocate' | 'clerk' = 'admin') {
  return useQuery({
    queryKey: queryKeys.reports.dashboardAdmin(),
    queryFn:  () => adminReportApi.getDashboardAnalytics(role),
    select:   (res) => res.data,
  })
}

export function useCaseStatusDistribution() {
  return useQuery({
    queryKey: queryKeys.reports.caseStatus(),
    queryFn:  () => adminReportApi.getCaseStatusDistribution(),
    select:   (res) => res.data,
  })
}

export function useCaseBacklog() {
  return useQuery({
    queryKey: queryKeys.reports.caseBacklog(),
    queryFn:  () => adminReportApi.getCaseBacklog(),
    select:   (res) => res.data,
  })
}

export function useJudgeReports() {
  return useQuery({
    queryKey: queryKeys.reports.judges(),
    queryFn:  () => adminReportApi.getJudgeReports(),
    select:   (res) => res.data,
  })
}

export function useJudgePerformanceScores() {
  return useQuery({
    queryKey: queryKeys.reports.judgeScores(),
    queryFn:  () => adminReportApi.getJudgePerformanceScores(),
    select:   (res) => res.data,
  })
}

export function useCourtReports() {
  return useQuery({
    queryKey: queryKeys.reports.courts(),
    queryFn:  () => adminReportApi.getCourtReports(),
    select:   (res) => res.data,
  })
}

export function usePerformanceMonthly() {
  return useQuery({
    queryKey: queryKeys.reports.perfMonthly(),
    queryFn:  () => adminReportApi.getPerformanceMonthly(),
    select:   (res) => res.data,
  })
}

export function usePerformanceQuarterly() {
  return useQuery({
    queryKey: queryKeys.reports.perfQuarterly(),
    queryFn:  () => adminReportApi.getPerformanceQuarterly(),
    select:   (res) => res.data,
  })
}

export function useAIAnalyticsReport() {
  return useQuery({
    queryKey: queryKeys.reports.aiAnalytics(),
    queryFn:  () => adminReportApi.getAIAnalytics(),
    select:   (res) => res.data,
  })
}

export function useResearchAnalytics() {
  return useQuery({
    queryKey: queryKeys.reports.research(),
    queryFn:  () => adminReportApi.getResearchAnalytics(),
    select:   (res) => res.data,
  })
}

export function useExportReport() {
  return useMutation({
    mutationFn: (body: ExportReportRequest) => adminReportApi.exportReport(body),
  })
}
