// ─── useVirtualization — Phase F3 ─────────────────────────────────────────────
// Thin wrapper around @tanstack/react-virtual for consistent API
import { useRef } from 'react'
import { useVirtualizer } from '@tanstack/react-virtual'

export interface UseVirtualizationOptions {
  count:       number
  estimateSize?: (index: number) => number
  overscan?:   number
  horizontal?: boolean
}

export function useVirtualization({
  count,
  estimateSize = () => 52,   // default row height
  overscan     = 10,
  horizontal   = false,
}: UseVirtualizationOptions) {
  const containerRef = useRef<HTMLDivElement | null>(null)

  const virtualizer = useVirtualizer({
    count,
    getScrollElement: () => containerRef.current,
    estimateSize,
    overscan,
    horizontal,
  })

  return {
    containerRef,
    virtualizer,
    virtualItems:  virtualizer.getVirtualItems(),
    totalSize:     virtualizer.getTotalSize(),
    scrollToIndex: virtualizer.scrollToIndex.bind(virtualizer),
  }
}
