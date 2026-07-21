// ─── useSearch — Phase F3 ─────────────────────────────────────────────────────
import { useState, useCallback, useRef, useEffect } from 'react'
import { useDebounce } from './useDebounce'

const HISTORY_KEY = 'tc_search_history'
const SAVED_KEY   = 'tc_saved_searches'
const MAX_HISTORY = 10

export interface UseSearchOptions {
  debounce?:     number    // ms
  storageKey?:   string    // scoped key prefix for localStorage
  minLength?:    number    // min chars to trigger search
  onSearch?:     (query: string) => void
}

export function useSearch(options: UseSearchOptions = {}) {
  const {
    debounce:   debounceMs = 300,
    storageKey  = 'global',
    minLength   = 0,
    onSearch,
  } = options

  const [query,    setQuery]    = useState('')
  const [isActive, setIsActive] = useState(false)

  const debouncedQuery = useDebounce(query, debounceMs)

  // Emit search when debounced value changes (and meets minLength)
  const prevDebouncedRef = useRef('')
  useEffect(() => {
    if (debouncedQuery !== prevDebouncedRef.current && debouncedQuery.length >= minLength) {
      prevDebouncedRef.current = debouncedQuery
      onSearch?.(debouncedQuery)
      if (debouncedQuery.trim()) addToHistory(debouncedQuery.trim(), storageKey)
    }
  }, [debouncedQuery, minLength, onSearch, storageKey])

  const clear   = useCallback(() => { setQuery(''); setIsActive(false) }, [])
  const focus   = useCallback(() => setIsActive(true), [])
  const blur    = useCallback(() => setIsActive(false), [])

  // History
  const [history, setHistory] = useState<string[]>(() => loadHistory(storageKey))
  const clearHistory = useCallback(() => {
    saveHistory(storageKey, [])
    setHistory([])
  }, [storageKey])

  // Saved searches
  const [saved, setSaved] = useState<string[]>(() => loadSaved(storageKey))
  const saveSearch  = useCallback((q: string) => {
    setSaved((prev) => {
      const next = [q, ...prev.filter((s) => s !== q)].slice(0, 20)
      saveSaved(storageKey, next)
      return next
    })
  }, [storageKey])
  const removeSaved = useCallback((q: string) => {
    setSaved((prev) => { const next = prev.filter((s) => s !== q); saveSaved(storageKey, next); return next })
  }, [storageKey])

  return {
    query, debouncedQuery, isActive,
    setQuery, clear, focus, blur,
    history, clearHistory,
    saved, saveSearch, removeSaved,
  }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────
function historyKey(scope: string) { return `${HISTORY_KEY}_${scope}` }
function savedKey(scope: string)   { return `${SAVED_KEY}_${scope}` }

function loadHistory(scope: string): string[] {
  try { return JSON.parse(localStorage.getItem(historyKey(scope)) ?? '[]') } catch { return [] }
}
function saveHistory(scope: string, h: string[]) {
  try { localStorage.setItem(historyKey(scope), JSON.stringify(h)) } catch {}
}
function addToHistory(query: string, scope: string) {
  const prev = loadHistory(scope)
  const next = [query, ...prev.filter((q) => q !== query)].slice(0, MAX_HISTORY)
  saveHistory(scope, next)
}
function loadSaved(scope: string): string[] {
  try { return JSON.parse(localStorage.getItem(savedKey(scope)) ?? '[]') } catch { return [] }
}
function saveSaved(scope: string, s: string[]) {
  try { localStorage.setItem(savedKey(scope), JSON.stringify(s)) } catch {}
}
