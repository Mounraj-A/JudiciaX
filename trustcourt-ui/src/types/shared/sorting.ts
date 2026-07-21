// ─── Sorting Types — Phase F3 ─────────────────────────────────────────────────

export type SortDirection = 'asc' | 'desc' | 'none'

export interface SortField {
  field:     string
  direction: SortDirection
  label?:    string  // human-readable column name
}

export interface SortState {
  sorts:      SortField[]
  multiSort:  boolean
}

export interface SortConfig {
  multiSort?:     boolean
  defaultSorts?:  SortField[]
  maxSorts?:      number  // limit for multi-sort
}

export interface SortOption {
  field:     string
  label:     string
  type?:     'string' | 'number' | 'date' | 'boolean'
  compareFn?: (a: unknown, b: unknown) => number  // custom comparator
}

export type SortRegistry = Record<string, SortOption>

/** Server-ready sort params sent to API */
export interface ServerSortParams {
  sortBy?:  string
  sortDir?: 'ASC' | 'DESC'
  sorts?:   { field: string; direction: 'ASC' | 'DESC' }[]  // for multi-sort
}
