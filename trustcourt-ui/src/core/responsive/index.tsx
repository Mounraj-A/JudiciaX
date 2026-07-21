// ─── Responsive & Breakpoint System ───────────────────────────────────────────
import React, { createContext, useContext, useState, useEffect } from 'react'

export type Breakpoint = 'xs' | 'sm' | 'md' | 'lg' | 'xl' | '2xl' | '3xl'

interface BreakpointState {
  current: Breakpoint
  width:   number
  isMobile: boolean
  isTablet: boolean
  isDesktop: boolean
}

const BreakpointContext = createContext<BreakpointState | null>(null)

function getBreakpoint(width: number): Breakpoint {
  if (width >= 1920) return '3xl'
  if (width >= 1536) return '2xl'
  if (width >= 1280) return 'xl'
  if (width >= 1024) return 'lg'
  if (width >= 768)  return 'md'
  if (width >= 640)  return 'sm'
  return 'xs'
}

export function BreakpointProvider({ children }: { children: React.ReactNode }) {
  const [state, setState] = useState<BreakpointState>(() => {
    const w = typeof window !== 'undefined' ? window.innerWidth : 1200
    const b = getBreakpoint(w)
    return {
      current: b,
      width:   w,
      isMobile: ['xs', 'sm'].includes(b),
      isTablet: b === 'md',
      isDesktop: ['lg', 'xl', '2xl', '3xl'].includes(b),
    }
  })

  useEffect(() => {
    let timeoutId: ReturnType<typeof setTimeout> | null = null
    const handleResize = () => {
      if (timeoutId) clearTimeout(timeoutId)
      timeoutId = setTimeout(() => {
        const w = window.innerWidth
        const b = getBreakpoint(w)
        setState({
          current: b,
          width:   w,
          isMobile: ['xs', 'sm'].includes(b),
          isTablet: b === 'md',
          isDesktop: ['lg', 'xl', '2xl', '3xl'].includes(b),
        })
      }, 100)
    }

    window.addEventListener('resize', handleResize)
    return () => window.removeEventListener('resize', handleResize)
  }, [])

  return (
    <BreakpointContext.Provider value={state}>
      {children}
    </BreakpointContext.Provider>
  )
}

export function useBreakpoint(): BreakpointState {
  const ctx = useContext(BreakpointContext)
  if (!ctx) throw new Error('useBreakpoint must be used within BreakpointProvider')
  return ctx
}
