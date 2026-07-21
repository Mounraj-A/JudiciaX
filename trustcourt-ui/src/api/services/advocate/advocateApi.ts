import { springApiClient } from '@/api/client/springApiClient'
import type { ApiResponse } from '@/types/api'

// ── Types ─────────────────────────────────────────────────────────────

export interface AdvocateProfileResponse {
  uuid: string
  fullName: string
  email: string
  phoneNumber: string
  barCouncilNumber: string
  enrollmentDate: string
  stateBarCouncil: string
  lawFirm: string
  specialization: string
  yearsOfPractice: number
  officeAddress: string
  officeCity: string
  officeState: string
  officePincode: string
  profilePhotoPath: string
  digitalSignaturePath: string
  verificationStatus: 'PENDING' | 'APPROVED' | 'REJECTED'
  verifiedAt: string
  createdAt: string
  updatedAt: string
}

export interface UpdateAdvocateProfileRequest {
  barCouncilNumber?: string
  enrollmentDate?: string
  stateBarCouncil?: string
  lawFirm?: string
  specialization?: string
  yearsOfPractice?: number
  officeAddress?: string
  officeCity?: string
  officeState?: string
  officePincode?: string
}

export interface AdvocateDashboardResponse {
  totalCases: number
  activeCases: number
  draftCases: number
  todayHearings: number
  unreadNotifications: number
  pendingTasks: number
  upcomingHearings: Array<{
    uuid: string
    caseNumber: string
    hearingDate: string
    courtRoom: string
    type: string
  }>
  recentActivities: Array<{
    uuid: string
    title: string
    description: string
    timestamp: string
    type: string
  }>
}

// ── API Functions ───────────────────────────────────────────────────────

/**
 * Retrieves the profile of the currently logged-in advocate.
 */
export const getMyProfile = async (): Promise<AdvocateProfileResponse> => {
  const { data } = await springApiClient.get<ApiResponse<AdvocateProfileResponse>>('/advocate/profile')
  return data.data
}

/**
 * Updates the advocate's profile.
 */
export const updateMyProfile = async (
  request: UpdateAdvocateProfileRequest
): Promise<AdvocateProfileResponse> => {
  const { data } = await springApiClient.put<ApiResponse<AdvocateProfileResponse>>(
    '/advocate/profile',
    request
  )
  return data.data
}

/**
 * Retrieves the advocate dashboard metrics and summaries.
 */
export const getDashboard = async (): Promise<AdvocateDashboardResponse> => {
  const { data } = await springApiClient.get<ApiResponse<AdvocateDashboardResponse>>('/advocate/dashboard')
  return data.data
}
