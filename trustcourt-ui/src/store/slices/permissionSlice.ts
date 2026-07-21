// ─── Redux Permission Slice ───────────────────────────────────────────────────
// Phase F2 – Resolved permission state
//
// ARCHITECTURE: Permissions are resolved ONCE on login from ROLE_REGISTRY.
// They live here and ONLY here. Guards, hooks, and contexts all read from
// this slice. There is no duplication across stores.
//
// Flow:
//   authService.login() → permissionService.resolvePermissions(role)
//     → ROLE_REGISTRY[role].permissions → permissionSlice.setResolvedPermissions()
//     → all guards/hooks read from selectResolvedPermissions
import { createSlice, type PayloadAction } from '@reduxjs/toolkit'
import type { PermissionString, RoleDefinition } from '@/types/permissions'

interface PermissionState {
  resolvedPermissions: PermissionString[]
  roleDefinition:      RoleDefinition | null
  isLoading:           boolean
}

const initialState: PermissionState = {
  resolvedPermissions: [],
  roleDefinition:      null,
  isLoading:           false,
}

const permissionSlice = createSlice({
  name: 'permission',
  initialState,
  reducers: {
    setResolvedPermissions(
      state,
      action: PayloadAction<{ permissions: PermissionString[]; roleDefinition: RoleDefinition }>
    ) {
      state.resolvedPermissions = action.payload.permissions
      state.roleDefinition      = action.payload.roleDefinition
      state.isLoading           = false
    },
    setPermissionLoading(state, action: PayloadAction<boolean>) {
      state.isLoading = action.payload
    },
    clearPermissions() {
      return { ...initialState }
    },
  },
})

export const {
  setResolvedPermissions,
  setPermissionLoading,
  clearPermissions,
} = permissionSlice.actions

export const permissionReducer = permissionSlice.reducer

// ─── Permission Selectors ─────────────────────────────────────────────────────
import type { RootState } from '../index'

export const selectResolvedPermissions = (s: RootState) => s.permission.resolvedPermissions
export const selectRoleDefinition      = (s: RootState) => s.permission.roleDefinition
export const selectPermissionLoading   = (s: RootState) => s.permission.isLoading
export const selectHomeRoute           = (s: RootState) => s.permission.roleDefinition?.homeRoute ?? '/dashboard'
