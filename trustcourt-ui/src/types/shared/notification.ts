// ─── Notification Types — Phase F3 ────────────────────────────────────────────

export type NotificationCategory =
  | 'system' | 'alert' | 'info' | 'success' | 'warning' | 'error'
  | 'case' | 'document' | 'hearing' | 'decision' | 'ai' | 'audit'

export type NotificationPriority = 'low' | 'normal' | 'high' | 'critical'

export interface Notification {
  id:          string
  title:       string
  message:     string
  category:    NotificationCategory
  priority:    NotificationPriority
  read:        boolean
  createdAt:   string   // ISO
  expiresAt?:  string   // ISO
  link?:       string   // relative URL to navigate on click
  actor?:      { id: string; name: string; avatar?: string }
  metadata?:   Record<string, unknown>
  actions?:    NotificationAction[]
}

export interface NotificationAction {
  id:      string
  label:   string
  variant: 'primary' | 'secondary' | 'danger'
  onClick: () => void
}

export interface NotificationState {
  items:      Notification[]
  unreadCount:number
  isOpen:     boolean   // drawer open/closed
  isLoading:  boolean
}

export interface NotificationFilter {
  category?:  NotificationCategory[]
  priority?:  NotificationPriority[]
  read?:      boolean
  dateFrom?:  string
  dateTo?:    string
}
