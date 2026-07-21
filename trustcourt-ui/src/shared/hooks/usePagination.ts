// ─── usePagination — Phase F3 ─────────────────────────────────────────────────
import { useState, useCallback, useMemo } from 'react'
import type { PaginationState, PaginationMeta, PaginationConfig } from '@/types/shared/pagination'

export function usePagination(
  total:  number,
  config: PaginationConfig = {},
): PaginationState & PaginationMeta & {
  setPage:        (page: number) => void
  setPageSize:    (size: number) => void
  nextPage:       () => void
  prevPage:       () => void
  firstPage:      () => void
  lastPage:       () => void
  pageSizeOptions:number[]
  reset:          () => void
} {
  const {
    initialPage     = 1,
    initialPageSize = 20,
    pageSizeOptions = [10, 20, 50, 100],
  } = config

  const [page, setPageRaw]     = useState(initialPage)
  const [pageSize, setPageSize] = useState(initialPageSize)

  const totalPages = Math.max(1, Math.ceil(total / pageSize))

  const setPage = useCallback((p: number) => {
    setPageRaw(Math.max(1, Math.min(p, Math.max(1, Math.ceil(total / pageSize)))))
  }, [total, pageSize])

  const nextPage  = useCallback(() => setPage(page + 1), [page, setPage])
  const prevPage  = useCallback(() => setPage(page - 1), [page, setPage])
  const firstPage = useCallback(() => setPage(1), [setPage])
  const lastPage  = useCallback(() => setPage(totalPages), [totalPages, setPage])
  const reset     = useCallback(() => { setPageRaw(1); setPageSize(initialPageSize) }, [initialPageSize])

  const meta = useMemo<PaginationMeta>(() => {
    const firstItem = total === 0 ? 0 : (page - 1) * pageSize + 1
    const lastItem  = Math.min(page * pageSize, total)
    return {
      currentPage:   page,
      pageSize,
      totalItems:    total,
      totalPages,
      hasNextPage:   page < totalPages,
      hasPrevPage:   page > 1,
      firstItem,
      lastItem,
    }
  }, [page, pageSize, total, totalPages])

  return {
    page, total,
    ...meta,
    setPage, setPageSize,
    nextPage, prevPage, firstPage, lastPage,
    pageSizeOptions,
    reset,
  }
}
