// â”€â”€â”€ SearchContext â€” Phase F3 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import React, { createContext, useContext } from 'react'
import { useSearch, type UseSearchOptions } from '@/shared/hooks/useSearch'

type SearchContextValue = ReturnType<typeof useSearch>

const SearchContext = createContext<SearchContextValue | null>(null)

interface SearchProviderProps extends UseSearchOptions {
  children: React.ReactNode
}

export function SearchProvider({ children, ...options }: SearchProviderProps) {
  const search = useSearch(options)
  return <SearchContext.Provider value={search}>{children}</SearchContext.Provider>
}

export function useSearchContext(): SearchContextValue {
  const ctx = useContext(SearchContext)
  if (!ctx) throw new Error('useSearchContext must be used inside <SearchProvider>')
  return ctx
}
