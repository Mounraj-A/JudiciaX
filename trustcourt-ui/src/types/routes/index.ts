// ─── Route & Navigation Types ─────────────────────────────────────────────────
import type { UserRole } from '../auth'
import type { PermissionString } from '../permissions'

export interface RouteConfig {
  path:         string
  name:         string
  element?:     React.LazyExoticComponent<React.ComponentType>
  layout?:      LayoutType
  guard?:       GuardType
  roles?:       UserRole[]
  permissions?: PermissionString[]
  meta?: {
    title:        string
    description?: string
    breadcrumb?:  string[]
    icon?:        string
    hideSidebar?: boolean
    hideTopNav?:  boolean
  }
  children?: RouteConfig[]
}

export type LayoutType =
  | 'public'
  | 'protected'
  | 'workspace'
  | 'dashboard'
  | 'blank'
  | 'error'

export type GuardType =
  | 'auth'
  | 'guest'
  | 'role'
  | 'permission'
  | 'none'

export interface NavigationItem {
  id:           string
  label:        string
  icon?:        string
  path?:        string
  roles?:       UserRole[]
  permissions?: PermissionString[]
  badge?:       string | number
  children?:    NavigationItem[]
  divider?:     boolean
  section?:     string
}

export interface BreadcrumbItem {
  label: string
  path?: string
}
