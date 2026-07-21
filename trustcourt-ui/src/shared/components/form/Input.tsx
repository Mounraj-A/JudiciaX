// ─── Input — Phase F3 ─────────────────────────────────────────────────────────
import React, { forwardRef } from 'react'
import { FieldWrapper } from './FieldWrapper'
import type { FieldWrapperProps as _FieldWrapperProps } from './FieldWrapper'

export interface InputProps extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'className'> {
  label?:       string
  error?:       string | null
  description?: string
  leftIcon?:    React.ReactNode
  rightIcon?:   React.ReactNode
  wrapperClass?:string
  inputClass?:  string
}

export const Input = forwardRef<HTMLInputElement, InputProps>(({
  label, error, description, leftIcon, rightIcon, wrapperClass, inputClass = '', required, disabled, ...props
}, ref) => {
  return (
    <FieldWrapper label={label} error={error} description={description} required={required} className={wrapperClass}>
      <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
        {leftIcon && <div style={{ position: 'absolute', left: '0.75rem', color: '#9CA3AF', display: 'flex' }}>{leftIcon}</div>}
        <input
          ref={ref}
          disabled={disabled}
          required={required}
          className={inputClass}
          style={{
            width: '100%',
            padding: `0.5rem ${rightIcon ? '2.5rem' : '0.75rem'} 0.5rem ${leftIcon ? '2.5rem' : '0.75rem'}`,
            fontSize: '0.875rem', color: '#111827',
            background: disabled ? '#F3F4F6' : '#FFFFFF',
            border: `1px solid ${error ? '#DC2626' : '#D1D5DB'}`,
            borderRadius: '0.375rem',
            outline: 'none',
            transition: 'border-color 0.15s, box-shadow 0.15s',
            cursor: disabled ? 'not-allowed' : 'text'
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
        {rightIcon && <div style={{ position: 'absolute', right: '0.75rem', color: '#9CA3AF', display: 'flex' }}>{rightIcon}</div>}
      </div>
    </FieldWrapper>
  )
})
Input.displayName = 'Input'
