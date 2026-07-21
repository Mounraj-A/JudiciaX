// ─── Drawer — Phase F3 ────────────────────────────────────────────────────────
import React, { useEffect } from 'react'

export interface DrawerProps {
  isOpen:    boolean
  onClose:   () => void
  title?:    string
  children:  React.ReactNode
  footer?:   React.ReactNode
  position?: 'left' | 'right'
  size?:     'sm' | 'md' | 'lg' | 'full'
  closeOnBg?:boolean
}

const SIZE_MAP = { sm: '320px', md: '480px', lg: '640px', full: '100vw' }

export function Drawer({ isOpen, onClose, title, children, footer, position = 'right', size = 'md', closeOnBg = true }: DrawerProps) {
  useEffect(() => {
    if (isOpen) document.body.style.overflow = 'hidden'
    else document.body.style.overflow = ''
    return () => { document.body.style.overflow = '' }
  }, [isOpen])

  useEffect(() => {
    const handleEsc = (e: KeyboardEvent) => { if (e.key === 'Escape' && isOpen) onClose() }
    window.addEventListener('keydown', handleEsc)
    return () => window.removeEventListener('keydown', handleEsc)
  }, [isOpen, onClose])

  if (!isOpen) return null

  return (
    <div style={{
      position: 'fixed', inset: 0, zIndex: 9999, display: 'flex',
      justifyContent: position === 'right' ? 'flex-end' : 'flex-start'
    }}>
      <div
        style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.5)', transition: 'opacity 0.2s' }}
        onClick={() => closeOnBg && onClose()}
      />
      <div style={{
        position: 'relative', width: SIZE_MAP[size], maxWidth: '100vw',
        background: '#FFFFFF', height: '100%', display: 'flex', flexDirection: 'column',
        boxShadow: position === 'right' ? '-4px 0 24px rgba(0,0,0,0.1)' : '4px 0 24px rgba(0,0,0,0.1)',
        animation: position === 'right' ? 'slideInRight 0.3s cubic-bezier(0.4, 0, 0.2, 1)' : 'slideInLeft 0.3s cubic-bezier(0.4, 0, 0.2, 1)'
      }}>
        {title && (
          <div style={{
            padding: '1.25rem 1.5rem', borderBottom: '1px solid #E5E7EB',
            display: 'flex', alignItems: 'center', justifyContent: 'space-between'
          }}>
            <h2 style={{ fontSize: '1.125rem', fontWeight: 600, color: '#111827', margin: 0 }}>{title}</h2>
            <button onClick={onClose} style={{ background: 'none', border: 'none', fontSize: '1.25rem', color: '#9CA3AF', cursor: 'pointer', padding: 0 }}>×</button>
          </div>
        )}
        <div style={{ padding: '1.5rem', overflowY: 'auto', flex: 1 }}>
          {children}
        </div>
        {footer && (
          <div style={{ padding: '1.25rem 1.5rem', borderTop: '1px solid #E5E7EB', background: '#F9FAFB', display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
            {footer}
          </div>
        )}
      </div>
      <style>{`
        @keyframes slideInRight { from { transform: translateX(100%) } to { transform: translateX(0) } }
        @keyframes slideInLeft  { from { transform: translateX(-100%) } to { transform: translateX(0) } }
      `}</style>
    </div>
  )
}
