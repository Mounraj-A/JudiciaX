// ─── Table Utilities — Phase F3 ───────────────────────────────────────────────
import type { ColumnDef } from '@/types/shared/table'
import type { SortField }  from '@/types/shared/sorting'

/** Sort an array by a list of SortField descriptors */
export function sortData<T extends Record<string, unknown>>(
  data:  T[],
  sorts: SortField[],
): T[] {
  if (sorts.length === 0) return data
  return [...data].sort((a, b) => {
    for (const { field, direction } of sorts) {
      if (direction === 'none') continue
      const av = a[field]
      const bv = b[field]
      let cmp  = 0
      if (av == null && bv == null) cmp = 0
      else if (av == null) cmp = 1
      else if (bv == null) cmp = -1
      else if (typeof av === 'string' && typeof bv === 'string') cmp = av.localeCompare(bv)
      else cmp = av < bv ? -1 : av > bv ? 1 : 0
      if (cmp !== 0) return direction === 'asc' ? cmp : -cmp
    }
    return 0
  })
}

/** Paginate a data array (client-side) */
export function paginateData<T>(data: T[], page: number, pageSize: number): T[] {
  const start = (page - 1) * pageSize
  return data.slice(start, start + pageSize)
}

/** Get only visible column definitions */
export function getVisibleColumns<T>(
  columns: ColumnDef<T>[],
  visibleIds: string[],
): ColumnDef<T>[] {
  return columns.filter((c) => visibleIds.includes(c.id) || c.visible !== false)
}

/** Build CSV string from data + column defs */
export function buildCSV<T extends Record<string, unknown>>(
  data:    T[],
  columns: ColumnDef<T>[],
): string {
  const exportable = columns.filter((c) => c.exportable !== false && c.type !== 'actions')
  const header     = exportable.map((c) => `"${c.header}"`).join(',')
  const rows       = data.map((row) =>
    exportable.map((c) => {
      const val = c.field ? row[c.field] : ''
      const str = c.exportFn ? c.exportFn(val, row) : String(val ?? '')
      return `"${String(str).replace(/"/g, '""')}"`
    }).join(',')
  )
  return [header, ...rows].join('\n')
}

/** Calculate column widths for a fixed-layout table */
export function calcColumnWidths<T>(columns: ColumnDef<T>[], totalWidth: number): number[] {
  const defined = columns.reduce((s, c) => s + (c.width ?? 0), 0)
  const flexible = columns.filter((c) => !c.width)
  const rem      = Math.max(0, totalWidth - defined) / Math.max(1, flexible.length)
  return columns.map((c) => c.width ?? rem)
}
