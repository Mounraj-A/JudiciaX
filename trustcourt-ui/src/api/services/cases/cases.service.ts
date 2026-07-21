import { springApiClient as apiClient } from '@/api/client/springApiClient'

// TODO: Phase F2 – Implement Cases API service endpoint
export const casesService = {
  list: (params?: Record<string, unknown>) => apiClient.get('/cases', { params }),
  get:  (id: string) => apiClient.get(`/cases/${id}`),
  upsertDraft: (data: any) => apiClient.post('/cases/draft', data),
  submit: (id: string) => apiClient.put(`/cases/${id}/submit`),
  uploadDocument: (data: FormData) => apiClient.post('/documents', data, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  uploadEvidence: (data: FormData) => apiClient.post('/evidence', data, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
}
