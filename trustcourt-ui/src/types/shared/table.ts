// ─── Table Types — Phase F3 ───────────────────────────────────────────────────
import type { SortDirection } from './sorting'

export type ColumnType = 'text' | 'number' | 'date' | 'boolean' | 'badge' | 'avatar' | 'actions' | 'custom'
export type ColumnPin  = 'left' | 'right' | false

export interface ColumnDef<TData = unknown> {
  id:          string
  field?:      keyof TData & string
  header:      string
  type?:       ColumnType
  width?:      number
  minWidth?:   number
  maxWidth?:   number
  sortable?:   boolean
  filterable?: boolean
  visible?:    boolean
  pin?:        ColumnPin
  resizable?:  boolean
  renderCell?: (value: unknown, row: TData) => React.ReactNode
  renderHeader?: () => React.ReactNode
  align?:      'left' | 'center' | 'right'
  exportable?: boolean
  exportFn?:   (value: unknown, row: TData) => string | number
}

export interface RowSelectionState {
  selectedIds:    Set<string>
  isAllSelected:  boolean
  isIndeterminate:boolean
}

export interface GridState<TData = unknown> {
  rows:         TData[]
  columns:      ColumnDef<TData>[]
  selection:    RowSelectionState
  visibleCols:  string[]
  pinnedLeft:   string[]
  pinnedRight:  string[]
  isLoading:    boolean
  error?:       string | null
}

export interface BulkAction {
  id:       string
  label:    string
  icon?:    string
  variant?: 'primary' | 'danger' | 'secondary'
  onClick:  (selectedIds: string[]) => void
  disabled?: (selectedIds: string[]) => boolean
}

export interface DataGridProps<TData = unknown> {
  data:           TData[]
  columns:        ColumnDef<TData>[]
  rowId:          keyof TData & string
  loading?:       boolean
  error?:         string | null
  emptyMessage?:  string
  selectable?:    boolean
  bulkActions?:   BulkAction[]
  sortable?:      boolean
  filterable?:    boolean
  exportable?:    boolean
  virtualise?:    boolean
  stickyHeader?:  boolean
  onRowClick?:    (row: TData) => void
  className?:     string
}

// Re-export for convenience
export type { SortDirection }
