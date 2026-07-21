// ─── Admin Configuration API ──────────────────────────────────────────────────
import { springApi } from '@/api/client/springApiClient'
import type { ApiResponse } from '@/types/api'

export interface ConfigurationResponse {
  uuid: string
  configKey: string
  configValue: string
  description: string
  dataType: string
  isEncrypted: boolean
  isActive: boolean
  createdAt: string
  updatedAt: string
}

export interface ConfigurationRequest {
  configKey: string
  configValue: string
  description?: string
  dataType: string
  isEncrypted?: boolean
}

export const adminConfigApi = {
  listConfigurations: () =>
    springApi.get<ApiResponse<ConfigurationResponse[]>>('/admin/configurations'),

  getConfigByUuid: (uuid: string) =>
    springApi.get<ApiResponse<ConfigurationResponse>>(`/admin/configurations/${uuid}`),

  getConfigByKey: (key: string) =>
    springApi.get<ApiResponse<ConfigurationResponse>>(`/admin/configurations/key/${key}`),

  createConfiguration: (data: ConfigurationRequest) =>
    springApi.post<ApiResponse<ConfigurationResponse>>('/admin/configurations', data),

  updateConfiguration: (uuid: string, data: ConfigurationRequest) =>
    springApi.put<ApiResponse<ConfigurationResponse>>(`/admin/configurations/${uuid}`, data),

  deleteConfiguration: (uuid: string) =>
    springApi.delete<ApiResponse<void>>(`/admin/configurations/${uuid}`),
}
