import { springApiClient } from '@/api/client/springApiClient'
import type { ApiResponse, PaginatedResponse as PageResponse } from '@/types/api'

// ── Types ─────────────────────────────────────────────────────────────

export interface HearingResponse {
  uuid: string
  caseUuid: string
  caseNumber: string
  hearingDate: string
  hearingType: string
  courtRoom: string
  judgeName?: string
  status: string
  notes?: string
}

// ── API Functions ───────────────────────────────────────────────────────

export const getMyHearings = async (page = 0, size = 10, sortBy = 'hearingDate', direction = 'asc'): Promise<PageResponse<HearingResponse>> => {
  const { data } = await springApiClient.get<ApiResponse<PageResponse<HearingResponse>>>('/advocate/hearings', {
    params: { page, size, sortBy, direction }
  })
  return data.data
}

export const getHearingDetails = async (hearingUuid: string): Promise<HearingResponse> => {
  const { data } = await springApiClient.get<ApiResponse<HearingResponse>>(`/advocate/hearings/${hearingUuid}`)
  return data.data
}
