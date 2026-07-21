// ─── Modal — Phase F3 ─────────────────────────────────────────────────────────
import React, { useEffect } from 'react'

export interface ModalProps {
  isOpen:    boolean
  onClose:   () => void
  title?:    string
  children:  React.ReactNode
  footer?:   React.ReactNode
  size?:     'sm' | 'md' | 'lg' | 'xl' | 'full'
  closeOnBg?:boolean
}

const SIZE_MAP = {
  sm: '400px', md: '600px', lg: '800px', xl: '1140px', full: 'calc(100vw - 2rem)'
}

export function Modal({ isOpen, onClose, title, children, footer, size = 'md', closeOnBg = true }: ModalProps) {
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
      position: 'fixed', inset: 0, zIndex: 9999,
      display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '1rem'
    }}>
      <div
        style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.5)', transition: 'opacity 0.2s' }}
        onClick={() => closeOnBg && onClose()}
      />
      <div style={{
        position: 'relative', width: SIZE_MAP[size], maxWidth: '100%',
        maxHeight: 'calc(100vh - 2rem)', background: '#FFFFFF', borderRadius: '0.75rem',
        boxShadow: '0 20px 25px -5px rgba(0,0,0,0.1), 0 10px 10px -5px rgba(0,0,0,0.04)',
        display: 'flex', flexDirection: 'column',
        animation: 'modalScaleIn 0.2s cubic-bezier(0.16, 1, 0.3, 1)'
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
          <div style={{ padding: '1.25rem 1.5rem', borderTop: '1px solid #E5E7EB', background: '#F9FAFB', borderRadius: '0 0 0.75rem 0.75rem', display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
            {footer}
          </div>
        )}
      </div>
      <style>{`@keyframes modalScaleIn { from { transform: scale(0.95); opacity: 0 } to { transform: scale(1); opacity: 1 } }`}</style>
    </div>
  )
}
