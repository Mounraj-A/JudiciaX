// â”€â”€â”€ Toast â€” Phase F3 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import { useEffect, useState } from 'react'

export type ToastVariant = 'success' | 'error' | 'warning' | 'info'

export interface ToastProps {
  id:       string
  title:    string
  message?: string
  variant?: ToastVariant
  duration?:number
  onClose:  (id: string) => void
}

const VARIANTS = {
  success: { bg: '#D1FAE5', color: '#065F46', icon: 'âœ“', border: '#10B981' },
  error:   { bg: '#FEE2E2', color: '#991B1B', icon: 'âš ', border: '#EF4444' },
  warning: { bg: '#FEF3C7', color: '#92400E', icon: '!', border: '#F59E0B' },
  info:    { bg: '#DBEAFE', color: '#1E40AF', icon: 'â„¹', border: '#3B82F6' }
}

export function Toast({ id, title, message, variant = 'info', duration = 5000, onClose }: ToastProps) {
  const [visible, setVisible] = useState(false)
  const v = VARIANTS[variant]

  useEffect(() => {
    // entry animation trigger
    requestAnimationFrame(() => setVisible(true))
    const timer = setTimeout(() => {
      setVisible(false)
      setTimeout(() => onClose(id), 300) // wait for exit animation
    }, duration)
    return () => clearTimeout(timer)
  }, [id, duration, onClose])

  return (
    <div style={{
      background: '#FFFFFF', border: '1px solid #E5E7EB', borderLeft: `4px solid ${v.border}`,
      borderRadius: '0.375rem', boxShadow: '0 10px 15px -3px rgba(0,0,0,0.1)',
      padding: '0.75rem 1rem', width: '300px', pointerEvents: 'auto',
      display: 'flex', gap: '0.75rem', alignItems: 'flex-start',
      transform: visible ? 'translateX(0)' : 'translateX(120%)',
      opacity: visible ? 1 : 0,
      transition: 'transform 0.3s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.3s ease',
      marginBottom: '0.5rem'
    }}>
      <div style={{
        width: 20, height: 20, borderRadius: '50%', background: v.bg, color: v.color,
        display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '0.75rem',
        fontWeight: 'bold', flexShrink: 0, marginTop: '0.125rem'
      }}>
        {v.icon}
      </div>
      <div style={{ flex: 1, minWidth: 0 }}>
        <p style={{ margin: 0, fontSize: '0.875rem', fontWeight: 600, color: '#111827' }}>{title}</p>
        {message && <p style={{ margin: '0.25rem 0 0', fontSize: '0.8125rem', color: '#6B7280' }}>{message}</p>}
      </div>
      <button
        onClick={() => { setVisible(false); setTimeout(() => onClose(id), 300) }}
        style={{ background: 'none', border: 'none', color: '#9CA3AF', cursor: 'pointer', padding: '0.125rem', fontSize: '1rem', lineHeight: 1 }}
      >Ã—</button>
    </div>
  )
}

// â”€â”€â”€ ToastProvider stub (in reality would be tied to context) â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
export function ToastContainer({ toasts, onClose }: { toasts: Omit<ToastProps, 'onClose'>[]; onClose: (id: string) => void }) {
  return (
    <div style={{
      position: 'fixed', top: '1.5rem', right: '1.5rem', zIndex: 9999,
      display: 'flex', flexDirection: 'column', pointerEvents: 'none'
    }}>
      {toasts.map((t) => <Toast key={t.id} {...t} onClose={onClose} />)}
    </div>
  )
}
