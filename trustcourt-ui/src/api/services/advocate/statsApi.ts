import { springApiClient } from '@/api/client/springApiClient'
import type { ApiResponse, PaginatedResponse as PageResponse } from '@/types/api'

export interface AdvocateCaseStatisticsResponse {
  totalCases: number
  activeCases: number
  draftCases: number
  pendingCases: number
  upcomingHearings: number
  disposedCases: number
}

export const getCaseStatistics = async (): Promise<AdvocateCaseStatisticsResponse> => {
  const { data } = await springApiClient.get<ApiResponse<AdvocateCaseStatisticsResponse>>('/advocate/cases/statistics')
  return data.data
}
