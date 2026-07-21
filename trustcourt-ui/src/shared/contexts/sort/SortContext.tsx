// ─── SortContext — Phase F3 ───────────────────────────────────────────────────
import React, { createContext, useContext } from 'react'
import { useSorting } from '@/shared/hooks/useSorting'
import type { SortConfig } from '@/types/shared/sorting'

type SortContextValue = ReturnType<typeof useSorting>
const SortContext = createContext<SortContextValue | null>(null)

interface SortProviderProps extends SortConfig { children: React.ReactNode }

export function SortProvider({ children, ...config }: SortProviderProps) {
  const sorting = useSorting(config)
  return <SortContext.Provider value={sorting}>{children}</SortContext.Provider>
}

export function useSortContext(): SortContextValue {
  const ctx = useContext(SortContext)
  if (!ctx) throw new Error('useSortContext must be used inside <SortProvider>')
  return ctx
}
