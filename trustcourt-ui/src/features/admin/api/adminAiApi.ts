// ─── Admin AI API ─────────────────────────────────────────────────────────────
import { springApi } from '@/api/client/springApiClient'
import type { ApiResponse } from '@/types/api'

export interface AISettingsResponse {
  aiEnabled: boolean
  modelVersion: string
  priorityThreshold: number
  confidenceThreshold: number
  explainabilityEnabled: boolean
  simulationMode: boolean
  maintenanceMode: boolean
  readOnlyMode: boolean
}

export interface AIUsageResponse {
  totalRequests: number
  successfulRequests: number
  failedRequests: number
  avgLatencyMs: number
  requestsToday: number
  ocrEnabled: boolean
  nlpEnabled: boolean
  priorityEngineEnabled: boolean
  ctsEnabled: boolean
}

export interface AIGatewayHealth {
  status: string
  version: string
  uptime: number
  services: Record<string, { status: string; latencyMs: number }>
}

export interface OCRQueueStatus {
  pending: number
  processing: number
  completed: number
  failed: number
  avgProcessingTimeMs: number
}

export const adminAiApi = {
  getAISettings: () =>
    springApi.get<ApiResponse<AISettingsResponse>>('/admin/ai/settings'),

  getAIUsage: () =>
    springApi.get<ApiResponse<AIUsageResponse>>('/admin/ai/usage'),

  enableAI: () =>
    springApi.put<ApiResponse<void>>('/admin/ai/enable'),

  disableAI: () =>
    springApi.put<ApiResponse<void>>('/admin/ai/disable'),

  setModelVersion: (version: string) =>
    springApi.put<ApiResponse<void>>('/admin/ai/model-version', null, {
      params: { version },
    }),

  setPriorityThreshold: (threshold: number) =>
    springApi.put<ApiResponse<void>>('/admin/ai/priority-threshold', null, {
      params: { threshold },
    }),

  setConfidenceThreshold: (threshold: number) =>
    springApi.put<ApiResponse<void>>('/admin/ai/confidence-threshold', null, {
      params: { threshold },
    }),

  setExplainability: (enabled: boolean) =>
    springApi.put<ApiResponse<void>>('/admin/ai/explainability', null, {
      params: { enabled },
    }),

  getAIGatewayHealth: () =>
    springApi.get<AIGatewayHealth>('/ai/health'),

  getAIVersion: () =>
    springApi.get<ApiResponse<{ version: string }>>('/ai/version'),

  getOCRQueue: () =>
    springApi.get<ApiResponse<OCRQueueStatus>>('/api/v1/ai/ocr/queue'),

  getOCRMonitor: () =>
    springApi.get<ApiResponse<Record<string, unknown>>>('/api/v1/ai/ocr/monitor'),

  getAIAnalytics: () =>
    springApi.get<ApiResponse<Record<string, unknown>>>('/api/reports/ai'),

  getAIQueueStatus: () =>
    springApi.get<ApiResponse<Record<string, unknown>>>('/api/reports/ai/queue-status'),
}
