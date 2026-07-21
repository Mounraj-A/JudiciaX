// ─── Advanced Layout Components — Phase F3 ────────────────────────────────────
import React, { useRef, useEffect } from 'react'
import { useDragResize } from '@/shared/hooks/useResize'

// ─── ResizablePanel ───────────────────────────────────────────────────────────
interface ResizablePanelProps {
  children:    React.ReactNode
  initialSize: number
  direction?:  'horizontal' | 'vertical'
  minSize?:    number
  maxSize?:    number
  className?:  string
}
export function ResizablePanel({
  children, initialSize, direction = 'horizontal', minSize = 200, maxSize = 800, className = ''
}: ResizablePanelProps) {
  const { panelSize, onMouseDown } = useDragResize(initialSize, direction, minSize, maxSize)
  const style = direction === 'horizontal'
    ? { width: panelSize, flexShrink: 0, overflow: 'hidden' }
    : { height: panelSize, flexShrink: 0, overflow: 'hidden' }
  const handleStyle: React.CSSProperties = direction === 'horizontal'
    ? { width: 4, cursor: 'col-resize', background: '#E5E7EB', flexShrink: 0, transition: 'background 0.2s' }
    : { height: 4, cursor: 'row-resize', background: '#E5E7EB', flexShrink: 0, transition: 'background 0.2s' }
  return (
    <>
      <div className={className} style={{ ...style, position: 'relative' }}>{children}</div>
      <div style={handleStyle} onMouseDown={onMouseDown} onMouseEnter={(e) => ((e.target as HTMLElement).style.background = '#0F1D3A')} onMouseLeave={(e) => ((e.target as HTMLElement).style.background = '#E5E7EB')} />
    </>
  )
}

// ─── SplitView ───────────────────────────────────────────────────────────────
interface SplitViewProps {
  left:        React.ReactNode
  right:       React.ReactNode
  initialLeft?:number
  direction?:  'horizontal' | 'vertical'
  className?:  string
}
export function SplitView({ left, right, initialLeft = 320, direction = 'horizontal', className = '' }: SplitViewProps) {
  return (
    <div className={className} style={{ display: 'flex', flexDirection: direction === 'horizontal' ? 'row' : 'column', height: '100%', overflow: 'hidden' }}>
      <ResizablePanel initialSize={initialLeft} direction={direction} minSize={200} maxSize={700}>
        {left}
      </ResizablePanel>
      <div style={{ flex: 1, overflow: 'auto' }}>{right}</div>
    </div>
  )
}

// ─── StickyToolbar ────────────────────────────────────────────────────────────
interface StickyToolbarProps {
  children:   React.ReactNode
  top?:       number
  zIndex?:    number
  bordered?:  boolean
  className?: string
}
export function StickyToolbar({ children, top = 0, zIndex = 10, bordered = true, className = '' }: StickyToolbarProps) {
  return (
    <div className={className} style={{
      position: 'sticky', top, zIndex,
      background: '#FFFFFF',
      ...(bordered ? { borderBottom: '1px solid #E5E7EB' } : {}),
      padding: '0.75rem 1rem',
    }}>
      {children}
    </div>
  )
}

// ─── FloatingActionBar ────────────────────────────────────────────────────────
interface FloatingActionBarProps {
  visible:    boolean
  children:   React.ReactNode
  position?:  'bottom' | 'top'
  className?: string
}
export function FloatingActionBar({ visible, children, position = 'bottom', className = '' }: FloatingActionBarProps) {
  return (
    <div className={className} style={{
      position: 'fixed',
      [position]: '1.5rem',
      left: '50%',
      zIndex: 50,
      background: '#0F1D3A',
      color: '#FFFFFF',
      borderRadius: '0.75rem',
      padding: '0.75rem 1.25rem',
      boxShadow: '0 8px 24px rgba(15,29,58,0.35)',
      display: 'flex', alignItems: 'center', gap: '0.75rem',
      transition: 'opacity 0.2s, transform 0.2s',
      opacity: visible ? 1 : 0,
      pointerEvents: visible ? 'auto' : 'none',
      transform: visible
        ? 'translateX(-50%) translateY(0)'
        : `translateX(-50%) translateY(${position === 'bottom' ? '1rem' : '-1rem'})`,
    }}>
      {children}
    </div>
  )
}

// ─── ContextMenu ─────────────────────────────────────────────────────────────
export interface ContextMenuItem {
  id:        string
  label:     string
  icon?:     string
  onClick:   () => void
  danger?:   boolean
  disabled?: boolean
  divider?:  boolean
}
interface ContextMenuProps {
  items:     ContextMenuItem[]
  x:         number
  y:         number
  onClose:   () => void
}
export function ContextMenu({ items, x, y, onClose }: ContextMenuProps) {
  const ref = useRef<HTMLDivElement>(null)
  useEffect(() => {
    const handler = (e: MouseEvent) => { if (ref.current && !ref.current.contains(e.target as Node)) onClose() }
    document.addEventListener('mousedown', handler)
    return () => document.removeEventListener('mousedown', handler)
  }, [onClose])

  return (
    <div ref={ref} style={{
      position: 'fixed', left: x, top: y, zIndex: 9999,
      background: '#FFFFFF', borderRadius: '0.5rem',
      border: '1px solid #E5E7EB', boxShadow: '0 8px 24px rgba(0,0,0,0.12)',
      minWidth: '180px', padding: '0.25rem 0', overflow: 'hidden',
    }}>
      {items.map((item) => item.divider
        ? <div key={item.id} style={{ height: 1, background: '#F3F4F6', margin: '0.25rem 0' }} />
        : (
          <button key={item.id} disabled={item.disabled} onClick={() => { item.onClick(); onClose() }} style={{
            display: 'block', width: '100%', textAlign: 'left',
            padding: '0.5rem 0.875rem', fontSize: '0.8125rem', border: 'none', cursor: item.disabled ? 'not-allowed' : 'pointer',
            background: 'none', color: item.danger ? '#DC2626' : item.disabled ? '#D1D5DB' : '#111827',
            fontWeight: 400, transition: 'background 0.1s',
          }}
          onMouseEnter={(e) => ((e.target as HTMLElement).style.background = item.danger ? '#FEF2F2' : '#F9FAFB')}
          onMouseLeave={(e) => ((e.target as HTMLElement).style.background = 'none')}
          >
            {item.label}
          </button>
        )
      )}
    </div>
  )
}
