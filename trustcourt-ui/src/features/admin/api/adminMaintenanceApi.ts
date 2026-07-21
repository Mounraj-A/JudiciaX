// ─── Admin Maintenance API ────────────────────────────────────────────────────
import { springApi } from '@/api/client/springApiClient'
import type { ApiResponse } from '@/types/api'

export interface MaintenanceResponse {
  uuid: string
  title: string
  description: string
  status: 'SCHEDULED' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED'
  scheduledStart: string
  scheduledEnd: string
  actualStart?: string
  actualEnd?: string
  affectedServices: string[]
  createdByUuid: string
  createdAt: string
}

export interface MaintenanceRequest {
  title: string
  description: string
  scheduledStart: string
  scheduledEnd: string
  affectedServices: string[]
}

export const adminMaintenanceApi = {
  listMaintenance: () =>
    springApi.get<ApiResponse<MaintenanceResponse[]>>('/admin/maintenance'),

  getMaintenanceByUuid: (uuid: string) =>
    springApi.get<ApiResponse<MaintenanceResponse>>(`/admin/maintenance/${uuid}`),

  createMaintenance: (data: MaintenanceRequest) =>
    springApi.post<ApiResponse<MaintenanceResponse>>('/admin/maintenance', data),

  updateMaintenance: (uuid: string, data: MaintenanceRequest) =>
    springApi.put<ApiResponse<MaintenanceResponse>>(`/admin/maintenance/${uuid}`, data),

  deleteMaintenance: (uuid: string) =>
    springApi.delete<ApiResponse<void>>(`/admin/maintenance/${uuid}`),

  activateMaintenance: (uuid: string) =>
    springApi.put<ApiResponse<void>>(`/admin/maintenance/${uuid}/activate`),

  completeMaintenance: (uuid: string) =>
    springApi.put<ApiResponse<void>>(`/admin/maintenance/${uuid}/complete`),

  cancelMaintenance: (uuid: string) =>
    springApi.put<ApiResponse<void>>(`/admin/maintenance/${uuid}/cancel`),
}
