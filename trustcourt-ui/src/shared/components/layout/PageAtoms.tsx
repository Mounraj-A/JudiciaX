// ─── Page Atoms — Phase F3 ────────────────────────────────────────────────────
import React from 'react'

// ─── PageTitle ─────────────────────────────────────────────────────────────
interface PageTitleProps { children: React.ReactNode; className?: string; as?: 'h1' | 'h2' | 'h3' }
export function PageTitle({ children, className = '', as: Tag = 'h1' }: PageTitleProps) {
  return (
    <Tag className={className} style={{
      fontSize: Tag === 'h1' ? '1.5rem' : Tag === 'h2' ? '1.25rem' : '1.125rem',
      fontWeight: 700, color: '#0F1D3A', lineHeight: 1.2,
      letterSpacing: '-0.01em', margin: 0,
    }}>
      {children}
    </Tag>
  )
}

// ─── PageDescription ────────────────────────────────────────────────────────
interface PageDescriptionProps { children: React.ReactNode; className?: string }
export function PageDescription({ children, className = '' }: PageDescriptionProps) {
  return (
    <p className={className} style={{ fontSize: '0.875rem', color: '#6B7280', margin: 0 }}>
      {children}
    </p>
  )
}

// ─── Divider ─────────────────────────────────────────────────────────────────
interface DividerProps {
  orientation?: 'horizontal' | 'vertical'
  label?:       string
  className?:   string
  spacing?:     string
}
export function Divider({ orientation = 'horizontal', label, className = '', spacing = '1.5rem' }: DividerProps) {
  if (orientation === 'vertical') {
    return <div className={className} style={{ width: 1, alignSelf: 'stretch', background: '#E5E7EB', margin: `0 ${spacing}` }} />
  }
  if (label) {
    return (
      <div className={`flex items-center gap-3 ${className}`} style={{ margin: `${spacing} 0` }}>
        <div style={{ flex: 1, height: 1, background: '#E5E7EB' }} />
        <span style={{ fontSize: '0.75rem', color: '#9CA3AF', fontWeight: 500, whiteSpace: 'nowrap' }}>{label}</span>
        <div style={{ flex: 1, height: 1, background: '#E5E7EB' }} />
      </div>
    )
  }
  return <div className={className} style={{ height: 1, background: '#E5E7EB', margin: `${spacing} 0` }} />
}
