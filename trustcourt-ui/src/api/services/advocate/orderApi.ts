import { springApiClient } from '@/api/client/springApiClient'
import type { ApiResponse, PaginatedResponse as PageResponse } from '@/types/api'

export interface JudgeOrderResponse {
  uuid: string
  caseUuid: string
  caseNumber: string
  orderType: string
  title: string
  orderText: string
  orderDate: string
  originalFileName?: string
  mimeType?: string
  storagePath?: string
  fileSizeBytes?: number
  isSigned: boolean
  remarks?: string
  createdAt: string
  updatedAt: string
}

export const getCaseOrders = async (caseUuid: string, page = 0, size = 10): Promise<PageResponse<JudgeOrderResponse>> => {
  const { data } = await springApiClient.get<ApiResponse<PageResponse<JudgeOrderResponse>>>(`/advocate/cases/${caseUuid}/orders`, {
    params: { page, size }
  })
  return data.data
}

export const getCaseJudgements = async (caseUuid: string, page = 0, size = 10): Promise<PageResponse<JudgeOrderResponse>> => {
  const { data } = await springApiClient.get<ApiResponse<PageResponse<JudgeOrderResponse>>>(`/advocate/cases/${caseUuid}/judgements`, {
    params: { page, size }
  })
  return data.data
}
