// ─── Label & Form Wrapper — Phase F3 ──────────────────────────────────────────
import React from 'react'

export interface FieldWrapperProps {
  label?:       string
  error?:       string | null
  description?: string
  required?:    boolean
  className?:   string
  children:     React.ReactNode
}

export function FieldWrapper({ label, error, description, required, className = '', children }: FieldWrapperProps) {
  return (
    <div className={className} style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem', marginBottom: '1rem' }}>
      {label && (
        <label style={{ fontSize: '0.875rem', fontWeight: 500, color: '#374151' }}>
          {label} {required && <span style={{ color: '#DC2626' }}>*</span>}
        </label>
      )}
      {children}
      {error && <span style={{ fontSize: '0.75rem', color: '#DC2626' }}>{error}</span>}
      {description && !error && <span style={{ fontSize: '0.75rem', color: '#6B7280' }}>{description}</span>}
    </div>
  )
}
