import { springApiClient } from '@/api/client/springApiClient'
import type { ApiResponse } from '@/types/api'

// ── Types ─────────────────────────────────────────────────────────────

export interface AiAnalysisResponse {
  caseUuid: string
  insights: string[]
  recommendedActions: string[]
  successProbability: number
  riskFactors: string[]
  generatedAt: string
}

// ── API Functions ───────────────────────────────────────────────────────

export const getAiAnalysis = async (caseUuid: string): Promise<AiAnalysisResponse> => {
  const { data } = await springApiClient.get<ApiResponse<AiAnalysisResponse>>(`/advocate/cases/${caseUuid}/ai-analysis`)
  return data.data
}
