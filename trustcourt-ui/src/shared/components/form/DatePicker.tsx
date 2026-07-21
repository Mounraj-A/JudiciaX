// ─── DatePicker — Phase F3 ────────────────────────────────────────────────────
import React, { forwardRef } from 'react'
import { FieldWrapper } from './FieldWrapper'

export interface DatePickerProps extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'type' | 'className'> {
  label?:       string
  error?:       string | null
  description?: string
  wrapperClass?:string
  inputClass?:  string
}

export const DatePicker = forwardRef<HTMLInputElement, DatePickerProps>(({
  label, error, description, wrapperClass, inputClass = '', required, disabled, ...props
}, ref) => {
  return (
    <FieldWrapper label={label} error={error} description={description} required={required} className={wrapperClass}>
      <input
        type="date"
        ref={ref}
        disabled={disabled}
        required={required}
        className={inputClass}
        style={{
          width: '100%',
          padding: '0.5rem 0.75rem',
          fontSize: '0.875rem', color: '#111827',
          background: disabled ? '#F3F4F6' : '#FFFFFF',
          border: `1px solid ${error ? '#DC2626' : '#D1D5DB'}`,
          borderRadius: '0.375rem',
          outline: 'none',
          transition: 'border-color 0.15s, box-shadow 0.15s',
          cursor: disabled ? 'not-allowed' : 'text',
          fontFamily: 'inherit'
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
      />
    </FieldWrapper>
  )
})
DatePicker.displayName = 'DatePicker'
