import React from 'react'
import { cn } from '@/shared/design-system/utils/cn'

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string
  helperText?: string
  error?: string
  leftElement?: React.ReactNode
  rightElement?: React.ReactNode
  inputSize?: 'sm' | 'md' | 'lg'
}

const Input = React.forwardRef<HTMLInputElement, InputProps>(
  (
    { className, label, helperText, error, leftElement, rightElement, inputSize = 'md', id, ...props },
    ref
  ) => {
    const inputId = id ?? label?.toLowerCase().replace(/\s+/g, '-')

    const sizeClasses = {
      sm: 'h-8 text-xs px-3',
      md: 'h-10 text-sm px-3',
      lg: 'h-12 text-base px-4',
    }

    return (
      <div className="flex flex-col gap-1.5 w-full">
        {label && (
          <label
            htmlFor={inputId}
            className="text-xs font-medium text-[#374151] uppercase tracking-wide"
          >
            {label}
          </label>
        )}
        <div className="relative flex items-center">
          {leftElement && (
            <div className="absolute left-3 flex items-center text-[#9CA3AF]" aria-hidden="true">
              {leftElement}
            </div>
          )}
          <input
            ref={ref}
            id={inputId}
            className={cn(
              'w-full rounded-[0.5rem] border bg-white text-[#111827]',
              'border-[#D1D5DB] placeholder:text-[#9CA3AF]',
              'transition-all duration-150',
              'focus:outline-none focus:ring-2 focus:ring-offset-1 focus:ring-[#1E3A8A] focus:border-transparent',
              'disabled:cursor-not-allowed disabled:bg-[#F9FAFB] disabled:opacity-60',
              error ? 'border-[#DC2626] focus:ring-[#DC2626]' : '',
              leftElement ? 'pl-9' : '',
              rightElement ? 'pr-9' : '',
              sizeClasses[inputSize],
              className
            )}
            aria-invalid={!!error}
            aria-describedby={
              error ? `${inputId}-error` : helperText ? `${inputId}-helper` : undefined
            }
            {...props}
          />
          {rightElement && (
            <div className="absolute right-3 flex items-center text-[#9CA3AF]">
              {rightElement}
            </div>
          )}
        </div>
        {error && (
          <p id={`${inputId}-error`} className="text-xs text-[#DC2626]" role="alert">
            {error}
          </p>
        )}
        {!error && helperText && (
          <p id={`${inputId}-helper`} className="text-xs text-[#6B7280]">
            {helperText}
          </p>
        )}
      </div>
    )
  }
)

Input.displayName = 'Input'
export { Input }
