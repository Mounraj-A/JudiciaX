import { apiClient } from '@/api/client/apiClient'

// TODO: Phase F1+ – Implement JPI API service endpoint
export const jpiService = {
  getScore: (caseId: string) => apiClient.get(`/jpi/${caseId}`),
}
