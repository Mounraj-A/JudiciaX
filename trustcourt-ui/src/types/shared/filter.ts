// ─── Filter Types — Phase F3 ──────────────────────────────────────────────────

export type FilterOperator =
  | 'eq' | 'neq'
  | 'contains' | 'not_contains' | 'starts_with' | 'ends_with'
  | 'gt' | 'gte' | 'lt' | 'lte' | 'between'
  | 'in' | 'not_in'
  | 'is_null' | 'is_not_null'
  | 'date_before' | 'date_after' | 'date_between' | 'date_on'

export type FilterLogic = 'AND' | 'OR'

export interface FilterValue {
  single?:  string | number | boolean
  range?:   [string | number, string | number]
  multi?:   (string | number)[]
  date?:    string  // ISO
  dates?:   [string, string]  // ISO range
}

export interface FilterField {
  id:        string
  field:     string
  label:     string
  operator:  FilterOperator
  value:     FilterValue
  type:      'text' | 'number' | 'date' | 'select' | 'multiselect' | 'boolean' | 'range'
}

export interface FilterState {
  filters: FilterField[]
  logic:   FilterLogic
  active:  boolean
}

export interface FilterConfig {
  fields:        FilterFieldConfig[]
  defaultLogic?: FilterLogic
  maxFilters?:   number
  persist?:      boolean  // save to localStorage
  storageKey?:   string
}

export interface FilterFieldConfig {
  field:     string
  label:     string
  type:      FilterField['type']
  operators?: FilterOperator[]
  options?:  { label: string; value: string | number }[]  // for select/multiselect
  min?:      number
  max?:      number
}

/** Server-ready filter params sent to API */
export interface ServerFilterParams {
  filters?: { field: string; op: string; value: unknown }[]
  logic?:   FilterLogic
  [key: string]: unknown  // flat params for simple APIs
}
