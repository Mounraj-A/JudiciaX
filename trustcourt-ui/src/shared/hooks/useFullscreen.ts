// ─── useFullscreen — Phase F3 ─────────────────────────────────────────────────
import { useState, useEffect, useCallback, useRef } from 'react'

export function useFullscreen() {
  const ref                         = useRef<HTMLElement | null>(null)
  const [isFullscreen, setFull]     = useState(false)

  useEffect(() => {
    const onChange = () => setFull(!!document.fullscreenElement)
    document.addEventListener('fullscreenchange', onChange)
    return () => document.removeEventListener('fullscreenchange', onChange)
  }, [])

  const enter = useCallback(async () => {
    const el = ref.current ?? document.documentElement
    await el.requestFullscreen?.()
  }, [])

  const exit   = useCallback(async () => { await document.exitFullscreen?.() }, [])
  const toggle = useCallback(async () => { isFullscreen ? await exit() : await enter() }, [isFullscreen, enter, exit])

  return { ref, isFullscreen, enter, exit, toggle }
}
