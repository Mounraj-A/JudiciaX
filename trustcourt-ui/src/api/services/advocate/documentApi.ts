import { springApiClient } from '@/api/client/springApiClient'
import type { ApiResponse, PaginatedResponse as PageResponse } from '@/types/api'

// ── Types ─────────────────────────────────────────────────────────────

export interface DocumentResponse {
  uuid: string
  caseUuid: string
  documentName: string
  documentType: string
  storagePath: string
  uploadedAt: string
  status: string
}

// ── API Functions ───────────────────────────────────────────────────────

export const uploadDocument = async (caseUuid: string, file: File, documentType: string): Promise<DocumentResponse> => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('documentType', documentType)

  const { data } = await springApiClient.post<ApiResponse<DocumentResponse>>(`/advocate/cases/${caseUuid}/documents`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  return data.data
}

export const getCaseDocuments = async (caseUuid: string, page = 0, size = 10): Promise<PageResponse<DocumentResponse>> => {
  const { data } = await springApiClient.get<ApiResponse<PageResponse<DocumentResponse>>>(`/advocate/cases/${caseUuid}/documents`, {
    params: { page, size }
  })
  return data.data
}

export const getDocumentDetails = async (caseUuid: string, docUuid: string): Promise<DocumentResponse> => {
  const { data } = await springApiClient.get<ApiResponse<DocumentResponse>>(`/advocate/cases/${caseUuid}/documents/${docUuid}`)
  return data.data
}

export const deleteDocument = async (caseUuid: string, docUuid: string): Promise<void> => {
  await springApiClient.delete(`/advocate/cases/${caseUuid}/documents/${docUuid}`)
}
