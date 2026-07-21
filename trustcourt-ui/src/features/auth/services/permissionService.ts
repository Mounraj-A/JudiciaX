// ─── Permission Service ───────────────────────────────────────────────────────
// Phase F2 – Resolves permissions from ROLE_REGISTRY and manages permissionSlice
//
// ARCHITECTURE:
//   - Permissions resolved ONCE at login from ROLE_REGISTRY
//   - Written to permissionSlice (single source of truth)
//   - All guards/hooks READ from permissionSlice, never re-resolve
//   - Future role added → update ROLE_REGISTRY only, no service changes needed
import type { AppDispatch } from '@/store'
import type { UserRole } from '@/types/auth'
import type { PermissionString } from '@/types/permissions'
import type { FeatureFlags } from '@/config/featureFlags'
import { ROLE_REGISTRY, hasPermission, hasAnyPermission, canAccessRoute } from '@/core/permissions'
import { setResolvedPermissions, clearPermissions } from '@/store/slices/permissionSlice'
import { permissionRegistry } from '@/core/permissions/permissionRegistry'
import { logger } from '@/core/logger'

class PermissionServiceClass {
  private _dispatch: AppDispatch | null = null

  init(dispatch: AppDispatch): void {
    this._dispatch = dispatch
  }

  private get dispatch(): AppDispatch {
    if (!this._dispatch) throw new Error('[PermissionService] Not initialised — call init(dispatch) first')
    return this._dispatch
  }

  /**
   * Resolve permissions for a role and write to permissionSlice.
   * Called once at login. All subsequent reads go through Redux.
   */
  resolvePermissions(role: UserRole): PermissionString[] {
    const definition = ROLE_REGISTRY[role]
    if (!definition) {
      logger.warn(`[PermissionService] No definition for role: ${role}`)
      this.dispatch(setResolvedPermissions({ permissions: [], roleDefinition: ROLE_REGISTRY['VIEWER'] }))
      return []
    }

    logger.info(`[PermissionService] Resolving permissions for ${role}: ${definition.permissions.length} permissions`)
    this.dispatch(setResolvedPermissions({
      permissions:    definition.permissions,
      roleDefinition: definition,
    }))
    return definition.permissions
  }

  clear(): void {
    this.dispatch(clearPermissions())
  }

  /** Check a single permission against a provided permissions array. */
  checkPermission(userPermissions: PermissionString[], permission: PermissionString): boolean {
    return hasPermission(userPermissions, permission)
  }

  /** Check any of a set of permissions. */
  checkAnyPermission(userPermissions: PermissionString[], permissions: PermissionString[]): boolean {
    return hasAnyPermission(userPermissions, permissions)
  }

  /** Check if a role can access a given route path. */
  checkRoute(role: UserRole, path: string): boolean {
    return canAccessRoute(role, path)
  }

  /** Check if a component is allowed for the current user. */
  checkComponent(componentId: string, userPermissions: PermissionString[]): boolean {
    return permissionRegistry.check(componentId, userPermissions)
  }

  /** Check a feature flag value. */
  checkFeature(flag: keyof FeatureFlags, flags: Partial<FeatureFlags>): boolean {
    return flags[flag] === true
  }
}

export const permissionService = new PermissionServiceClass()
