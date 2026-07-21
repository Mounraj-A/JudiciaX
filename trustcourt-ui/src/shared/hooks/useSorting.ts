// ─── useSorting — Phase F3 ────────────────────────────────────────────────────
import { useState, useCallback, useMemo } from 'react'
import type { SortField, SortState, SortConfig, ServerSortParams } from '@/types/shared/sorting'

export function useSorting(config: SortConfig = {}) {
  const { multiSort = false, defaultSorts = [], maxSorts = 3 } = config

  const [sorts, setSorts] = useState<SortField[]>(defaultSorts)

  const sortBy = useCallback((field: string, label?: string) => {
    setSorts((prev) => {
      const existing = prev.find((s) => s.field === field)
      if (existing) {
        if (existing.direction === 'asc')  return prev.map((s) => s.field === field ? { ...s, direction: 'desc' as const } : s)
        if (existing.direction === 'desc') return prev.filter((s) => s.field !== field)
      }
      const newSort: SortField = { field, direction: 'asc', label }
      if (!multiSort) return [newSort]
      return prev.length < maxSorts ? [...prev, newSort] : prev
    })
  }, [multiSort, maxSorts])

  const clearSort  = useCallback((field?: string) => {
    setSorts((prev) => field ? prev.filter((s) => s.field !== field) : [])
  }, [])

  const getDirection = useCallback((field: string) => {
    return sorts.find((s) => s.field === field)?.direction ?? 'none'
  }, [sorts])

  const state: SortState = useMemo(() => ({ sorts, multiSort }), [sorts, multiSort])

  const serverParams: ServerSortParams = useMemo(() => {
    if (sorts.length === 0) return {}
    if (!multiSort) {
      return { sortBy: sorts[0].field, sortDir: sorts[0].direction.toUpperCase() as 'ASC' | 'DESC' }
    }
    return { sorts: sorts.map((s) => ({ field: s.field, direction: s.direction.toUpperCase() as 'ASC' | 'DESC' })) }
  }, [sorts, multiSort])

  return { sorts, state, sortBy, clearSort, getDirection, serverParams }
}
