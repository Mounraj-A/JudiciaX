// ─── Textarea — Phase F3 ──────────────────────────────────────────────────────
import React, { forwardRef } from 'react'
import { FieldWrapper } from './FieldWrapper'

export interface TextareaProps extends Omit<React.TextareaHTMLAttributes<HTMLTextAreaElement>, 'className'> {
  label?:       string
  error?:       string | null
  description?: string
  wrapperClass?:string
  textareaClass?:string
}

export const Textarea = forwardRef<HTMLTextAreaElement, TextareaProps>(({
  label, error, description, wrapperClass, textareaClass = '', required, disabled, rows = 4, ...props
}, ref) => {
  return (
    <FieldWrapper label={label} error={error} description={description} required={required} className={wrapperClass}>
      <textarea
        ref={ref}
        disabled={disabled}
        required={required}
        rows={rows}
        className={textareaClass}
        style={{
          width: '100%',
          padding: '0.5rem 0.75rem',
          fontSize: '0.875rem', color: '#111827',
          background: disabled ? '#F3F4F6' : '#FFFFFF',
          border: `1px solid ${error ? '#DC2626' : '#D1D5DB'}`,
          borderRadius: '0.375rem',
          outline: 'none',
          resize: 'vertical',
          fontFamily: 'inherit',
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
    </FieldWrapper>
  )
})
Textarea.displayName = 'Textarea'
