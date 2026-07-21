// ─── Admin Role API ───────────────────────────────────────────────────────────
import { springApi } from '@/api/client/springApiClient'
import type { ApiResponse } from '@/types/api'

export interface RoleResponse {
  uuid: string
  name: string
  description: string
  permissions: string[]
  createdAt: string
}

export interface RoleRequest {
  name: string
  description: string
}

export interface PermissionRequest {
  permissionCode: string
}

export const adminRoleApi = {
  listRoles: () =>
    springApi.get<ApiResponse<RoleResponse[]>>('/rbac/roles'),

  getRoleByUuid: (uuid: string) =>
    springApi.get<ApiResponse<RoleResponse>>(`/rbac/roles/${uuid}`),

  getRolePermissions: (uuid: string) =>
    springApi.get<ApiResponse<string[]>>(`/rbac/roles/${uuid}/permissions`),

  createRole: (data: RoleRequest) =>
    springApi.post<ApiResponse<RoleResponse>>('/rbac/roles', data),

  updateRole: (uuid: string, data: RoleRequest) =>
    springApi.put<ApiResponse<RoleResponse>>(`/rbac/roles/${uuid}`, data),

  deleteRole: (uuid: string) =>
    springApi.delete<ApiResponse<void>>(`/rbac/roles/${uuid}`),

  addPermission: (uuid: string, permissionCode: string) =>
    springApi.post<ApiResponse<void>>(`/rbac/roles/${uuid}/permissions`, { permissionCode }),

  removePermission: (uuid: string, permissionCode: string) =>
    springApi.delete<ApiResponse<void>>(`/rbac/roles/${uuid}/permissions/${permissionCode}`),
}
