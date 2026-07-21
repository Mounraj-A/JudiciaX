// ─── useResize — Phase F3 ─────────────────────────────────────────────────────
import { useState, useEffect, useRef, useCallback } from 'react'

export interface Size { width: number; height: number }

/** Observe element size changes */
export function useResize(initialSize?: Size) {
  const ref               = useRef<HTMLElement | null>(null)
  const [size, setSize]   = useState<Size>(initialSize ?? { width: 0, height: 0 })

  useEffect(() => {
    const el = ref.current
    if (!el) return
    const observer = new ResizeObserver(([entry]) => {
      const { width, height } = entry.contentRect
      setSize({ width: Math.round(width), height: Math.round(height) })
    })
    observer.observe(el)
    setSize({ width: el.clientWidth, height: el.clientHeight })
    return () => observer.disconnect()
  }, [])

  return { ref, size, width: size.width, height: size.height }
}

/** Draggable panel resize (returns delta position) */
export function useDragResize(
  initialSize: number,
  direction: 'horizontal' | 'vertical' = 'horizontal',
  minSize = 100,
  maxSize = Infinity,
) {
  const [panelSize, setPanelSize] = useState(initialSize)
  const dragging                  = useRef(false)
  const startPos                  = useRef(0)
  const startSize                 = useRef(initialSize)

  const onMouseDown = useCallback((e: React.MouseEvent) => {
    e.preventDefault()
    dragging.current  = true
    startPos.current  = direction === 'horizontal' ? e.clientX : e.clientY
    startSize.current = panelSize

    const onMove = (ev: MouseEvent) => {
      if (!dragging.current) return
      const delta = direction === 'horizontal' ? ev.clientX - startPos.current : ev.clientY - startPos.current
      setPanelSize(Math.min(maxSize, Math.max(minSize, startSize.current + delta)))
    }
    const onUp = () => { dragging.current = false; window.removeEventListener('mousemove', onMove); window.removeEventListener('mouseup', onUp) }
    window.addEventListener('mousemove', onMove)
    window.addEventListener('mouseup', onUp)
  }, [direction, minSize, maxSize, panelSize])

  return { panelSize, setPanelSize, onMouseDown }
}
