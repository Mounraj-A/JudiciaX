import { springApiClient } from '@/api/client/springApiClient'
import type { ApiResponse, PaginatedResponse as PageResponse } from '@/types/api'

export interface CaseNoteResponse {
  uuid: string
  caseUuid: string
  noteTitle: string
  noteContent: string
  createdAt: string
  updatedAt: string
}

export interface CreateCaseNoteRequest {
  noteTitle: string
  noteContent: string
}

export const getCaseNotes = async (caseUuid: string, page = 0, size = 10): Promise<PageResponse<CaseNoteResponse>> => {
  const { data } = await springApiClient.get<ApiResponse<PageResponse<CaseNoteResponse>>>(`/advocate/cases/${caseUuid}/notes`, {
    params: { page, size }
  })
  return data.data
}

export const createCaseNote = async (caseUuid: string, request: CreateCaseNoteRequest): Promise<CaseNoteResponse> => {
  const { data } = await springApiClient.post<ApiResponse<CaseNoteResponse>>(`/advocate/cases/${caseUuid}/notes`, request)
  return data.data
}

export const deleteCaseNote = async (caseUuid: string, noteUuid: string): Promise<void> => {
  await springApiClient.delete(`/advocate/cases/${caseUuid}/notes/${noteUuid}`)
}
