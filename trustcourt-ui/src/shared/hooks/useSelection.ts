// ─── useSelection — Phase F3 ──────────────────────────────────────────────────
import { useState, useCallback, useMemo } from 'react'

export interface UseSelectionReturn {
  selectedIds:     Set<string>
  isSelected:      (id: string) => boolean
  isAllSelected:   boolean
  isIndeterminate: boolean
  toggleItem:      (id: string) => void
  selectAll:       (ids: string[]) => void
  clearSelection:  () => void
  selectedCount:   number
  selectedArray:   string[]
}

export function useSelection(allIds: string[] = []): UseSelectionReturn {
  const [selectedIds, setSelectedIds] = useState<Set<string>>(new Set())

  const isSelected = useCallback((id: string) => selectedIds.has(id), [selectedIds])

  const toggleItem = useCallback((id: string) => {
    setSelectedIds((prev) => {
      const next = new Set(prev)
      next.has(id) ? next.delete(id) : next.add(id)
      return next
    })
  }, [])

  const selectAll = useCallback((ids: string[]) => {
    setSelectedIds((prev) => {
      const allSelected = ids.every((id) => prev.has(id))
      if (allSelected) {
        const next = new Set(prev)
        ids.forEach((id) => next.delete(id))
        return next
      }
      return new Set([...prev, ...ids])
    })
  }, [])

  const clearSelection = useCallback(() => setSelectedIds(new Set()), [])

  const isAllSelected   = allIds.length > 0 && allIds.every((id) => selectedIds.has(id))
  const isIndeterminate = !isAllSelected && allIds.some((id) => selectedIds.has(id))
  const selectedArray   = useMemo(() => Array.from(selectedIds), [selectedIds])

  return {
    selectedIds, isSelected,
    isAllSelected, isIndeterminate,
    toggleItem, selectAll, clearSelection,
    selectedCount: selectedIds.size,
    selectedArray,
  }
}
