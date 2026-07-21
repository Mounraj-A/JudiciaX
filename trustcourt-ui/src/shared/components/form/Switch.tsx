// ─── Switch — Phase F3 ────────────────────────────────────────────────────────
import React, { forwardRef } from 'react'

export interface SwitchProps extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'type' | 'className'> {
  label?:       string | React.ReactNode
  description?: string
  wrapperClass?:string
}

export const Switch = forwardRef<HTMLInputElement, SwitchProps>(({
  label, description, wrapperClass = '', disabled, checked, ...props
}, ref) => {
  return (
    <div className={wrapperClass} style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.75rem' }}>
      <label style={{ position: 'relative', display: 'inline-flex', alignItems: 'center', cursor: disabled ? 'not-allowed' : 'pointer' }}>
        <input
          type="checkbox"
          ref={ref}
          disabled={disabled}
          checked={checked}
          style={{ position: 'absolute', opacity: 0, width: 0, height: 0, pointerEvents: 'none' }}
          {...props}
        />
        <div style={{
          width: '2.5rem', height: '1.25rem', background: checked ? '#0F1D3A' : '#E5E7EB',
          borderRadius: '9999px', transition: 'background-color 0.2s', opacity: disabled ? 0.6 : 1
        }}>
          <div style={{
            position: 'absolute', top: '0.125rem', left: checked ? 'calc(100% - 1.125rem)' : '0.125rem',
            background: '#FFFFFF', width: '1rem', height: '1rem', borderRadius: '50%',
            transition: 'left 0.2s', boxShadow: '0 1px 3px rgba(0,0,0,0.2)'
          }} />
        </div>
      </label>
      {(label || description) && (
        <div style={{ display: 'flex', flexDirection: 'column' }}>
          {label && <span style={{ fontSize: '0.875rem', fontWeight: 500, color: disabled ? '#9CA3AF' : '#374151' }}>{label}</span>}
          {description && <span style={{ fontSize: '0.75rem', color: '#6B7280' }}>{description}</span>}
        </div>
      )}
    </div>
  )
})
Switch.displayName = 'Switch'
