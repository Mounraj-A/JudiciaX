// ─── Section & Container Components — Phase F3 ────────────────────────────────
import React from 'react'

// ─── Section ──────────────────────────────────────────────────────────────────
interface SectionProps {
  title?:     string
  subtitle?:  string
  actions?:   React.ReactNode
  children:   React.ReactNode
  bordered?:  boolean
  padded?:    boolean
  className?: string
  id?:        string
}
export function Section({ title, subtitle, actions, children, bordered = true, padded = true, className = '', id }: SectionProps) {
  return (
    <section id={id} className={className} style={{ marginBottom: '1.5rem' }}>
      {(title || actions) && (
        <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: '1rem', marginBottom: '1rem' }}>
          <div>
            {title && <h2 style={{ fontSize: '1rem', fontWeight: 600, color: '#111827', margin: 0 }}>{title}</h2>}
            {subtitle && <p style={{ fontSize: '0.8125rem', color: '#6B7280', marginTop: '0.25rem', marginBottom: 0 }}>{subtitle}</p>}
          </div>
          {actions && <div style={{ flexShrink: 0 }}>{actions}</div>}
        </div>
      )}
      <div style={{
        ...(bordered ? { border: '1px solid #E5E7EB', borderRadius: '0.75rem' } : {}),
        ...(padded   ? { padding: '1.25rem' } : {}),
        background: '#FFFFFF',
      }}>
        {children}
      </div>
    </section>
  )
}

// ─── CardSection ─────────────────────────────────────────────────────────────
interface CardSectionProps {
  title?:    string
  children:  React.ReactNode
  shadow?:   'sm' | 'md' | 'lg' | 'none'
  padding?:  string
  className?:string
  onClick?:  () => void
}
export function CardSection({ title, children, shadow = 'sm', padding = '1.25rem', className = '', onClick }: CardSectionProps) {
  const shadowMap = { none: 'none', sm: '0 1px 3px rgba(0,0,0,0.08)', md: '0 4px 12px rgba(0,0,0,0.1)', lg: '0 10px 24px rgba(0,0,0,0.12)' }
  return (
    <div
      className={className}
      onClick={onClick}
      style={{
        background: '#FFFFFF', borderRadius: '0.75rem',
        border: '1px solid #E5E7EB', padding,
        boxShadow: shadowMap[shadow],
        ...(onClick ? { cursor: 'pointer' } : {}),
      }}
    >
      {title && <div style={{ fontSize: '0.875rem', fontWeight: 600, color: '#4B5563', marginBottom: '0.75rem' }}>{title}</div>}
      {children}
    </div>
  )
}

// ─── GridLayout ─────────────────────────────────────────────────────────────
interface GridLayoutProps {
  children: React.ReactNode
  cols?:    1 | 2 | 3 | 4 | 5 | 6
  gap?:     string
  className?: string
}
export function GridLayout({ children, cols = 1, gap = '1.5rem', className = '' }: GridLayoutProps) {
  return (
    <div
      className={className}
      style={{
        display: 'grid',
        gridTemplateColumns: `repeat(${cols}, minmax(0, 1fr))`,
        gap,
      }}
    >
      {children}
    </div>
  )
}

// ─── ContentContainer ─────────────────────────────────────────────────────────
interface ContentContainerProps {
  children:   React.ReactNode
  maxWidth?:  string
  padding?:   string
  className?: string
}
export function ContentContainer({ children, maxWidth = '1280px', padding = '1.5rem', className = '' }: ContentContainerProps) {
  return (
    <div className={className} style={{ maxWidth, width: '100%', margin: '0 auto', padding }}>
      {children}
    </div>
  )
}

// ─── FilterContainer ─────────────────────────────────────────────────────────
interface FilterContainerProps { children: React.ReactNode; className?: string }
export function FilterContainer({ children, className = '' }: FilterContainerProps) {
  return (
    <div className={className} style={{
      display: 'flex', alignItems: 'center', gap: '0.75rem', flexWrap: 'wrap',
      padding: '0.75rem 1rem', background: '#F9FAFB', borderRadius: '0.625rem',
      border: '1px solid #E5E7EB', marginBottom: '1rem',
    }}>
      {children}
    </div>
  )
}

// ─── SearchContainer ─────────────────────────────────────────────────────────
interface SearchContainerProps { children: React.ReactNode; className?: string; maxWidth?: string }
export function SearchContainer({ children, className = '', maxWidth = '480px' }: SearchContainerProps) {
  return (
    <div className={className} style={{ maxWidth, width: '100%', position: 'relative' }}>
      {children}
    </div>
  )
}
