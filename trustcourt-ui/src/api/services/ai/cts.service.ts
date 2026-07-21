import { apiClient } from '@/api/client/apiClient'

// TODO: Phase F1+ – Implement CTS API service endpoint
export const ctsService = {
  getScore: (caseId: string) => apiClient.get(`/cts/${caseId}`),
}
