// ─── useInfiniteScroll — Phase F3 ─────────────────────────────────────────────
import { useEffect, useRef, useCallback } from 'react'

export interface UseInfiniteScrollOptions {
  onLoadMore:    () => void
  hasMore:       boolean
  isLoading:     boolean
  threshold?:    number   // 0-1, default 0.1
  rootMargin?:   string   // CSS margin, default '100px'
}

/**
 * Returns a sentinel ref to attach to the last item.
 * Calls onLoadMore when it enters the viewport.
 */
export function useInfiniteScroll({
  onLoadMore,
  hasMore,
  isLoading,
  threshold  = 0.1,
  rootMargin = '100px',
}: UseInfiniteScrollOptions) {
  const sentinelRef = useRef<HTMLDivElement | null>(null)

  const handleIntersection = useCallback<IntersectionObserverCallback>(
    (entries) => {
      const [entry] = entries
      if (entry.isIntersecting && hasMore && !isLoading) {
        onLoadMore()
      }
    },
    [hasMore, isLoading, onLoadMore],
  )

  useEffect(() => {
    const el = sentinelRef.current
    if (!el) return
    const observer = new IntersectionObserver(handleIntersection, {
      threshold,
      rootMargin,
    })
    observer.observe(el)
    return () => observer.disconnect()
  }, [handleIntersection, threshold, rootMargin])

  return { sentinelRef }
}
