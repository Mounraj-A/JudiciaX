// ─── State Layout Components — Phase F3 ───────────────────────────────────────
import React from 'react'

// ─── EmptyLayout ─────────────────────────────────────────────────────────────
interface EmptyLayoutProps {
  icon?:       React.ReactNode
  title?:      string
  description?:string
  action?:     React.ReactNode
  compact?:    boolean
  className?:  string
}
export function EmptyLayout({
  icon, title = 'No data found', description, action, compact = false, className = ''
}: EmptyLayoutProps) {
  const padding = compact ? '2rem' : '4rem'
  return (
    <div className={className} style={{
      display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
      padding, textAlign: 'center', gap: '0.75rem',
    }}>
      {icon && <div style={{ fontSize: '3rem', color: '#D1D5DB', marginBottom: '0.25rem' }}>{icon}</div>}
      <p style={{ fontSize: compact ? '0.9rem' : '1rem', fontWeight: 600, color: '#374151', margin: 0 }}>{title}</p>
      {description && <p style={{ fontSize: '0.8125rem', color: '#9CA3AF', maxWidth: '24rem', margin: 0 }}>{description}</p>}
      {action && <div style={{ marginTop: '0.5rem' }}>{action}</div>}
    </div>
  )
}

// ─── LoadingLayout ────────────────────────────────────────────────────────────
interface LoadingLayoutProps { message?: string; compact?: boolean; className?: string }
export function LoadingLayout({ message = 'Loading…', compact = false, className = '' }: LoadingLayoutProps) {
  return (
    <div className={className} style={{
      display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
      padding: compact ? '1.5rem' : '3rem', gap: '1rem',
    }}>
      <div style={{
        width: 36, height: 36, borderRadius: '50%',
        border: '3px solid #E5E7EB', borderTopColor: '#0F1D3A',
        animation: 'spin 0.8s linear infinite',
      }} />
      <p style={{ fontSize: '0.875rem', color: '#6B7280', margin: 0 }}>{message}</p>
      <style>{`@keyframes spin { to { transform: rotate(360deg) } }`}</style>
    </div>
  )
}

// ─── ErrorLayout ─────────────────────────────────────────────────────────────
interface ErrorLayoutProps {
  title?:     string
  message?:   string
  action?:    React.ReactNode
  compact?:   boolean
  className?: string
}
export function ErrorLayout({
  title = 'Something went wrong', message, action, compact = false, className = ''
}: ErrorLayoutProps) {
  return (
    <div className={className} style={{
      display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
      padding: compact ? '1.5rem' : '3rem', textAlign: 'center', gap: '0.75rem',
    }}>
      <div style={{
        width: 48, height: 48, borderRadius: '50%', background: '#FEE2E2',
        display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.5rem',
      }}>⚠</div>
      <p style={{ fontSize: '1rem', fontWeight: 600, color: '#DC2626', margin: 0 }}>{title}</p>
      {message && <p style={{ fontSize: '0.8125rem', color: '#6B7280', maxWidth: '24rem', margin: 0 }}>{message}</p>}
      {action && <div style={{ marginTop: '0.5rem' }}>{action}</div>}
    </div>
  )
}
