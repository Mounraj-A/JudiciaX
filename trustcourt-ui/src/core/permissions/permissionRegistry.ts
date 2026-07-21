// ─── Permission Registry ──────────────────────────────────────────────────────
// Phase F2 – Dynamic component-level permission registry
//
// Maps component/feature IDs to required permissions.
// Used by PermissionGuard for fine-grained UI gating.
import type { PermissionString } from '@/types/permissions'
import { logger } from '@/core/logger'

class PermissionRegistryClass {
  private readonly _registry = new Map<string, PermissionString>()

  /**
   * Register a component or feature with its required permission.
   * @param componentId - Unique identifier for the component (e.g. 'case-approve-btn')
   * @param required    - The permission string required to render/use this component
   */
  register(componentId: string, required: PermissionString): void {
    this._registry.set(componentId, required)
    logger.debug(`[PermissionRegistry] Registered: ${componentId} → ${required}`)
  }

  /**
   * Check if a component is accessible given user's permissions.
   * Returns true if the component is not registered (default allow).
   */
  check(componentId: string, userPermissions: PermissionString[]): boolean {
    const required = this._registry.get(componentId)
    if (!required) return true    // Not registered = not restricted
    return userPermissions.includes(required)
  }

  /** Get the required permission for a component ID. */
  resolve(componentId: string): PermissionString | undefined {
    return this._registry.get(componentId)
  }

  /** List all registered component IDs (for debugging). */
  list(): Record<string, string> {
    const result: Record<string, string> = {}
    this._registry.forEach((perm, id) => { result[id] = perm })
    return result
  }

  /** Unregister a component (e.g. on component unmount in dynamic UIs). */
  unregister(componentId: string): void {
    this._registry.delete(componentId)
  }
}

export const permissionRegistry = new PermissionRegistryClass()
