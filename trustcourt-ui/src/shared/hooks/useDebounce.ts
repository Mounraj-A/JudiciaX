// ─── useDebounce — Phase F3 ───────────────────────────────────────────────────
import { useState, useEffect, useRef, useCallback } from 'react'

/**
 * Debounces a value — returns the value only after `delay` ms of no changes.
 */
export function useDebounce<T>(value: T, delay = 300): T {
  const [debounced, setDebounced] = useState<T>(value)

  useEffect(() => {
    const id = setTimeout(() => setDebounced(value), delay)
    return () => clearTimeout(id)
  }, [value, delay])

  return debounced
}

/**
 * Returns a debounced callback. The callback is stable (memoized).
 */
export function useDebouncedCallback<A extends unknown[]>(
  fn:    (...args: A) => void,
  delay = 300,
): (...args: A) => void {
  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null)
  const fnRef    = useRef(fn)
  fnRef.current  = fn

  return useCallback((...args: A) => {
    if (timerRef.current) clearTimeout(timerRef.current)
    timerRef.current = setTimeout(() => fnRef.current(...args), delay)
  }, [delay])
}
