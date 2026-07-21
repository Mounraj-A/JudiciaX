// ─── useFiltering — Phase F3 ──────────────────────────────────────────────────
import { useState, useCallback, useMemo } from 'react'
import type { FilterField, FilterState, FilterLogic, FilterValue, ServerFilterParams } from '@/types/shared/filter'
import { v4 as uuid } from 'uuid'

export function useFiltering(initial?: Partial<FilterState>) {
  const [filters, setFilters] = useState<FilterField[]>(initial?.filters ?? [])
  const [logic,   setLogic]   = useState<FilterLogic>(initial?.logic ?? 'AND')

  const addFilter = useCallback((field: Omit<FilterField, 'id'>) => {
    setFilters((prev) => [...prev, { ...field, id: uuid() }])
  }, [])

  const updateFilter = useCallback((id: string, updates: Partial<Omit<FilterField, 'id'>>) => {
    setFilters((prev) => prev.map((f) => f.id === id ? { ...f, ...updates } : f))
  }, [])

  const removeFilter = useCallback((id: string) => {
    setFilters((prev) => prev.filter((f) => f.id !== id))
  }, [])

  const clearFilters = useCallback(() => setFilters([]), [])

  const setFilterValue = useCallback((id: string, value: FilterValue) => {
    updateFilter(id, { value })
  }, [updateFilter])

  const active = filters.length > 0

  const state: FilterState = useMemo(() => ({ filters, logic, active }), [filters, logic, active])

  const serverParams: ServerFilterParams = useMemo(() => ({
    filters: filters.map((f) => ({
      field: f.field,
      op:    f.operator,
      value: f.value.single ?? f.value.multi ?? f.value.range ?? f.value.date ?? f.value.dates,
    })),
    logic,
  }), [filters, logic])

  return {
    filters, logic, active, state,
    addFilter, updateFilter, removeFilter, clearFilters, setFilterValue,
    setLogic, serverParams,
  }
}
