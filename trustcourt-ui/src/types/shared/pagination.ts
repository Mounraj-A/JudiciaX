// ─── Pagination Types — Phase F3 ─────────────────────────────────────────────

export interface PaginationState {
  page:     number   // 1-based
  pageSize: number
  total:    number
}

export interface PaginationMeta {
  currentPage:  number
  pageSize:     number
  totalItems:   number
  totalPages:   number
  hasNextPage:  boolean
  hasPrevPage:  boolean
  firstItem:    number  // 1-based index of first item on page
  lastItem:     number  // 1-based index of last item on page
}

export interface PaginationConfig {
  initialPage?:      number
  initialPageSize?:  number
  pageSizeOptions?:  number[]
  defaultPageSize?:  number
}

export type PageChangeHandler = (page: number) => void
export type PageSizeChangeHandler = (pageSize: number) => void

export interface ServerPaginationParams {
  page:     number
  pageSize: number
  offset:   number  // for cursor/offset-based APIs
}
