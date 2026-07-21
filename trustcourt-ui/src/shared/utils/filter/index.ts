// ─── Filter Utilities — Phase F3 ──────────────────────────────────────────────
import type { FilterField, FilterLogic } from '@/types/shared/filter'

/** Apply filter fields to an array of items (client-side) */
export function applyFilters<T extends Record<string, unknown>>(
  items:   T[],
  filters: FilterField[],
  logic:   FilterLogic = 'AND',
): T[] {
  if (filters.length === 0) return items

  return items.filter((item) => {
    const results = filters.map((f) => evaluateFilter(item, f))
    return logic === 'AND' ? results.every(Boolean) : results.some(Boolean)
  })
}

function evaluateFilter<T extends Record<string, unknown>>(item: T, filter: FilterField): boolean {
  const raw = item[filter.field]
  const v   = filter.value

  switch (filter.operator) {
    case 'eq':           return raw === v.single
    case 'neq':          return raw !== v.single
    case 'contains':     return String(raw).toLowerCase().includes(String(v.single).toLowerCase())
    case 'not_contains': return !String(raw).toLowerCase().includes(String(v.single).toLowerCase())
    case 'starts_with':  return String(raw).toLowerCase().startsWith(String(v.single).toLowerCase())
    case 'ends_with':    return String(raw).toLowerCase().endsWith(String(v.single).toLowerCase())
    case 'gt':           return Number(raw) > Number(v.single)
    case 'gte':          return Number(raw) >= Number(v.single)
    case 'lt':           return Number(raw) < Number(v.single)
    case 'lte':          return Number(raw) <= Number(v.single)
    case 'between':      return v.range ? Number(raw) >= Number(v.range[0]) && Number(raw) <= Number(v.range[1]) : true
    case 'in':           return Array.isArray(v.multi) ? v.multi.includes(raw as string) : false
    case 'not_in':       return Array.isArray(v.multi) ? !v.multi.includes(raw as string) : true
    case 'is_null':      return raw == null
    case 'is_not_null':  return raw != null
    case 'date_before':  return v.date ? new Date(String(raw)) < new Date(v.date) : true
    case 'date_after':   return v.date ? new Date(String(raw)) > new Date(v.date) : true
    case 'date_on':      return v.date ? new Date(String(raw)).toDateString() === new Date(v.date).toDateString() : true
    case 'date_between': return v.dates ? new Date(String(raw)) >= new Date(v.dates[0]) && new Date(String(raw)) <= new Date(v.dates[1]) : true
    default:             return true
  }
}

/** Serialize filters to URL params */
export function filtersToParams(filters: FilterField[]): URLSearchParams {
  const p = new URLSearchParams()
  filters.forEach((f, i) => {
    p.set(`f[${i}][field]`, f.field)
    p.set(`f[${i}][op]`,    f.operator)
    const val = f.value.single ?? f.value.date ?? (f.value.multi ?? f.value.range ?? f.value.dates)?.join(',') ?? ''
    p.set(`f[${i}][val]`,   String(val))
  })
  return p
}
