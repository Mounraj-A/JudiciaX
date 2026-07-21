// ─── Admin Dashboard API ──────────────────────────────────────────────────────
import { springApi } from '@/api/client/springApiClient'
import type { ApiResponse } from '@/types/api'

// ── Response Types ─────────────────────────────────────────────────────────────
export interface UserSummaryResponse {
  uuid: string
  username: string
  email: string
  fullName: string
  role: string
  accountStatus: string
  createdAt: string
}

export interface AdminDashboardResponse {
  // User statistics
  totalUsers: number
  pendingApprovals: number
  lockedUsers: number
  activeUsers: number
  totalAdvocates: number
  totalJudges: number
  totalClerks: number
  totalAdmins: number
  // Court statistics
  totalCourts: number
  totalBenches: number
  totalCourtRooms: number
  // Case statistics
  registeredCases: number
  pendingCases: number
  disposedCases: number
  todayHearings: number
  // System statistics
  unreadNotifications: number
  auditEventsToday: number
  activeMaintenanceWindows: number
  activeAnnouncements: number
  // Security
  failedLoginsToday: number
  activeSessions: number
  highSeveritySecurityEvents: number
  // AI
  aiEnabled: boolean
  aiModelVersion: string
  aiAnalyzedCasesToday: number
  // Shortlists
  recentPendingUsers: UserSummaryResponse[]
}

// ── API Functions ──────────────────────────────────────────────────────────────
export const adminDashboardApi = {
  getDashboard: () =>
    springApi.get<ApiResponse<AdminDashboardResponse>>('/admin/dashboard'),
}
