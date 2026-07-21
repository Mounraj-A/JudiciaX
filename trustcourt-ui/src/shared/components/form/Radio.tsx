// ─── Radio — Phase F3 ─────────────────────────────────────────────────────────
import React, { forwardRef } from 'react'

export interface RadioProps extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'type' | 'className'> {
  label:        string | React.ReactNode
  description?: string
  wrapperClass?:string
}

export const Radio = forwardRef<HTMLInputElement, RadioProps>(({
  label, description, wrapperClass = '', disabled, ...props
}, ref) => {
  return (
    <div className={wrapperClass} style={{ display: 'flex', alignItems: 'flex-start', gap: '0.5rem', marginBottom: '0.75rem' }}>
      <div style={{ display: 'flex', alignItems: 'center', height: '1.25rem' }}>
        <input
          type="radio"
          ref={ref}
          disabled={disabled}
          style={{
            width: '1rem', height: '1rem', cursor: disabled ? 'not-allowed' : 'pointer',
            accentColor: '#0F1D3A', margin: 0
          }}
          {...props}
        />
      </div>
      <div style={{ display: 'flex', flexDirection: 'column' }}>
        <label
          style={{ fontSize: '0.875rem', fontWeight: 500, color: disabled ? '#9CA3AF' : '#374151', cursor: disabled ? 'not-allowed' : 'pointer' }}
          onClick={(e) => { e.preventDefault(); if (!disabled) (e.target as HTMLElement).parentElement?.previousSibling?.firstChild?.dispatchEvent(new MouseEvent('click', { bubbles: true })) }}
        >
          {label}
        </label>
        {description && <span style={{ fontSize: '0.75rem', color: '#6B7280' }}>{description}</span>}
      </div>
    </div>
  )
})
Radio.displayName = 'Radio'
