// ─── Select — Phase F3 ────────────────────────────────────────────────────────
import React, { forwardRef } from 'react'
import { FieldWrapper } from './FieldWrapper'

export interface SelectOption {
  label: string
  value: string | number
  disabled?: boolean
}

export interface SelectProps extends Omit<React.SelectHTMLAttributes<HTMLSelectElement>, 'className'> {
  label?:       string
  error?:       string | null
  description?: string
  options:      SelectOption[]
  placeholder?: string
  wrapperClass?:string
  selectClass?: string
}

export const Select = forwardRef<HTMLSelectElement, SelectProps>(({
  label, error, description, options, placeholder, wrapperClass, selectClass = '', required, disabled, ...props
}, ref) => {
  return (
    <FieldWrapper label={label} error={error} description={description} required={required} className={wrapperClass}>
      <select
        ref={ref}
        disabled={disabled}
        required={required}
        className={selectClass}
        style={{
          width: '100%',
          padding: '0.5rem 2.5rem 0.5rem 0.75rem',
          fontSize: '0.875rem', color: '#111827',
          background: disabled ? '#F3F4F6' : '#FFFFFF',
          border: `1px solid ${error ? '#DC2626' : '#D1D5DB'}`,
          borderRadius: '0.375rem',
          outline: 'none',
          appearance: 'none',
          cursor: disabled ? 'not-allowed' : 'pointer',
          backgroundImage: `url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="%236B7280"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/></svg>')`,
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'right 0.75rem center',
          backgroundSize: '1rem',
        }}
        onFocus={(e) => {
          if (!error && !disabled) {
            e.target.style.borderColor = '#0F1D3A'
            e.target.style.boxShadow = '0 0 0 1px #0F1D3A'
          }
        }}
        onBlur={(e) => {
          e.target.style.borderColor = error ? '#DC2626' : '#D1D5DB'
          e.target.style.boxShadow = 'none'
          props.onBlur?.(e)
        }}
        {...props}
      >
        {placeholder && <option value="" disabled>{placeholder}</option>}
        {options.map((opt) => (
          <option key={opt.value} value={opt.value} disabled={opt.disabled}>
            {opt.label}
          </option>
        ))}
      </select>
    </FieldWrapper>
  )
})
Select.displayName = 'Select'
