// ─── Tooltip — Phase F3 ───────────────────────────────────────────────────────
import React, { useState, useRef, useEffect } from 'react'

export interface TooltipProps {
  content:   React.ReactNode
  children:  React.ReactElement
  position?: 'top' | 'bottom' | 'left' | 'right'
  delay?:    number
  className?:string
}

export function Tooltip({ content, children, position = 'top', delay = 300, className = '' }: TooltipProps) {
  const [visible, setVisible] = useState(false)
  const timerRef              = useRef<ReturnType<typeof setTimeout> | null>(null)
  const triggerRef            = useRef<HTMLElement>(null)
  const tooltipRef            = useRef<HTMLDivElement>(null)

  const handleEnter = () => {
    if (timerRef.current) clearTimeout(timerRef.current)
    timerRef.current = setTimeout(() => setVisible(true), delay)
  }

  const handleLeave = () => {
    if (timerRef.current) clearTimeout(timerRef.current)
    setVisible(false)
  }

  useEffect(() => {
    return () => { if (timerRef.current) clearTimeout(timerRef.current) }
  }, [])

  // Position calculation
  const [coords, setCoords] = useState({ top: 0, left: 0 })
  useEffect(() => {
    if (visible && triggerRef.current && tooltipRef.current) {
      const trigger = triggerRef.current.getBoundingClientRect()
      const tooltip = tooltipRef.current.getBoundingClientRect()
      const gap = 8

      let top = 0
      let left = 0

      switch (position) {
        case 'top':
          top = trigger.top - tooltip.height - gap
          left = trigger.left + (trigger.width / 2) - (tooltip.width / 2)
          break
        case 'bottom':
          top = trigger.bottom + gap
          left = trigger.left + (trigger.width / 2) - (tooltip.width / 2)
          break
        case 'left':
          top = trigger.top + (trigger.height / 2) - (tooltip.height / 2)
          left = trigger.left - tooltip.width - gap
          break
        case 'right':
          top = trigger.top + (trigger.height / 2) - (tooltip.height / 2)
          left = trigger.right + gap
          break
      }

      setCoords({ top, left })
    }
  }, [visible, position])

  return (
    <>
      <span
        ref={triggerRef as React.RefObject<HTMLSpanElement>}
        onMouseEnter={handleEnter}
        onMouseLeave={handleLeave}
        onFocus={handleEnter}
        onBlur={handleLeave}
        style={{ display: 'contents' }}
      >
        {children}
      </span>
      {visible && (
        <div ref={tooltipRef} className={className} style={{
          position: 'fixed', top: coords.top, left: coords.left, zIndex: 9999,
          background: '#111827', color: '#FFFFFF', padding: '0.375rem 0.75rem',
          borderRadius: '0.375rem', fontSize: '0.75rem', fontWeight: 500,
          pointerEvents: 'none', whiteSpace: 'nowrap',
          boxShadow: '0 4px 6px -1px rgba(0,0,0,0.1)',
          animation: 'tooltipFadeIn 0.15s ease-out'
        }}>
          {content}
          <style>{`@keyframes tooltipFadeIn { from { opacity: 0; transform: scale(0.95) } to { opacity: 1; transform: scale(1) } }`}</style>
        </div>
      )}
    </>
  )
}
