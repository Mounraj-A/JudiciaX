// ─── useClipboard — Phase F3 ──────────────────────────────────────────────────
import { useState, useCallback } from 'react'

export function useClipboard(resetDelay = 2000) {
  const [copied, setCopied] = useState(false)
  const [error,  setError]  = useState<string | null>(null)

  const copy = useCallback(async (text: string) => {
    try {
      await navigator.clipboard.writeText(text)
      setCopied(true)
      setError(null)
      setTimeout(() => setCopied(false), resetDelay)
    } catch (e) {
      setError('Clipboard write failed')
      setCopied(false)
    }
  }, [resetDelay])

  const reset = useCallback(() => { setCopied(false); setError(null) }, [])

  return { copied, error, copy, reset }
}
