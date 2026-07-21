// ─── UI State Types ───────────────────────────────────────────────────────────
import type { ThemeName, DensityMode } from '@/shared/design-system/theme/ThemeProvider'

export interface UIState {
  theme:            ThemeName
  density:          DensityMode
  sidebarCollapsed: boolean
  sidebarMobileOpen:boolean
  globalLoading:    boolean
  loadingMessage:   string | null
  pageTitle:        string
  breadcrumbs:      BreadcrumbEntry[]
}

export interface BreadcrumbEntry {
  label: string
  path?: string
}

export interface NotificationItem {
  id:        string
  type:      'success' | 'error' | 'warning' | 'info'
  title?:    string
  message:   string
  duration?: number
  persistent?: boolean
  actions?:  { label: string; onClick: () => void }[]
  createdAt: number
}

export interface ModalState {
  id:      string
  isOpen:  boolean
  props?:  Record<string, unknown>
}
