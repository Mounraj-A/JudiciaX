// ─── PageHeader — Phase F3 ────────────────────────────────────────────────────
import React from 'react'

interface PageHeaderProps {
  title:        string
  description?: string
  subtitle?:    string
  actions?:     React.ReactNode
  breadcrumb?:  React.ReactNode
  badge?:       React.ReactNode
  className?:   string
}

export function PageHeader({ title, description, subtitle, actions, breadcrumb, badge, className = '' }: PageHeaderProps) {
  const desc = description || subtitle
  return (
    <div className={`page-header ${className}`} style={{ marginBottom: '1.5rem' }}>
      {breadcrumb && <div style={{ marginBottom: '0.5rem' }}>{breadcrumb}</div>}
      <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: '1rem', flexWrap: 'wrap' }}>
        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', flexWrap: 'wrap' }}>
            <h1 style={{
              fontSize: '1.5rem', fontWeight: 700, color: '#0F1D3A',
              lineHeight: 1.2, letterSpacing: '-0.01em', margin: 0,
            }}>
              {title}
            </h1>
            {badge}
          </div>
          {desc && (
            <p style={{ marginTop: '0.375rem', fontSize: '0.875rem', color: '#6B7280', margin: '0.375rem 0 0' }}>
              {desc}
            </p>
          )}
        </div>
        {actions && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', flexShrink: 0 }}>
            {actions}
          </div>
        )}
      </div>
    </div>
  )
}
