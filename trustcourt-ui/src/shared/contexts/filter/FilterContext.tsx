// ─── FilterContext — Phase F3 ─────────────────────────────────────────────────
import React, { createContext, useContext } from 'react'
import { useFiltering } from '@/shared/hooks/useFiltering'
import type { FilterState } from '@/types/shared/filter'

type FilterContextValue = ReturnType<typeof useFiltering>

const FilterContext = createContext<FilterContextValue | null>(null)

interface FilterProviderProps {
  initial?:  Partial<FilterState>
  children:  React.ReactNode
}

export function FilterProvider({ initial, children }: FilterProviderProps) {
  const filtering = useFiltering(initial)
  return <FilterContext.Provider value={filtering}>{children}</FilterContext.Provider>
}

export function useFilterContext(): FilterContextValue {
  const ctx = useContext(FilterContext)
  if (!ctx) throw new Error('useFilterContext must be used inside <FilterProvider>')
  return ctx
}
