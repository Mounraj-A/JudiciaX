// ─── Global API Types ─────────────────────────────────────────────────────────

export interface ApiResponse<T = unknown> {
  data: T
  status: number
  message: string
  timestamp: string
  requestId: string
}

export interface PaginatedResponse<T = unknown> {
  data: T[]
  pagination: {
    page:       number
    pageSize:   number
    total:      number
    totalPages: number
  }
}

export interface ApiError {
  code:       string
  message:    string
  details?:   Record<string, string[]>
  requestId?: string
  timestamp?: string
  path?:      string
  status:     number
}

export type ApiStatus = 'idle' | 'loading' | 'success' | 'error'

export interface QueryOptions {
  page?:     number
  pageSize?: number
  sort?:     string
  order?:    'asc' | 'desc'
  search?:   string
  filters?:  Record<string, unknown>
}
