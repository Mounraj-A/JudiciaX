import React from 'react'
import { cva, type VariantProps } from 'class-variance-authority'
import { cn } from '@/shared/design-system/utils/cn'

/** Button version: 1.0.0 */
export const BUTTON_VERSION = '1.0.0'

const buttonVariants = cva(
  [
    'inline-flex items-center justify-center gap-2',
    'rounded-[var(--tc-radius-button,0.5rem)]',
    'text-sm font-medium',
    'transition-all duration-200 ease-out',
    'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-[var(--tc-color-secondary-600)]',
    'disabled:pointer-events-none disabled:opacity-50',
    'select-none',
  ].join(' '),
  {
    variants: {
      variant: {
        primary: [
          'bg-[#0F1D3A] text-white',
          'hover:bg-[#1E4FA0] hover:-translate-y-px',
          'active:bg-[#080F1C] active:translate-y-0',
          'shadow-soft hover:shadow-medium',
        ].join(' '),
        secondary: [
          'bg-white text-[#0F1D3A] border border-[#E5E7EB]',
          'hover:bg-[#F8F9FA] hover:border-[#D1D5DB] hover:-translate-y-px',
          'shadow-soft',
        ].join(' '),
        accent: [
          'bg-[#C2410C] text-white',
          'hover:bg-[#9A3412] hover:-translate-y-px',
          'shadow-soft hover:shadow-medium',
        ].join(' '),
        ghost: [
          'bg-transparent text-[#374151]',
          'hover:bg-[#F3F4F6]',
        ].join(' '),
        destructive: [
          'bg-[#B91C1C] text-white',
          'hover:bg-[#991B1B] hover:-translate-y-px',
        ].join(' '),
        outline: [
          'bg-transparent text-[#0F1D3A] border-2 border-[#0F1D3A]',
          'hover:bg-[#0F1D3A] hover:text-white',
        ].join(' '),
        link: [
          'bg-transparent text-[#1E3A8A] underline-offset-4',
          'hover:underline',
        ].join(' '),
      },
      size: {
        sm:   'h-8  px-3 text-xs',
        md:   'h-10 px-4 text-sm',
        lg:   'h-12 px-6 text-base',
        icon: 'h-10 w-10 p-0',
      },
      fullWidth: {
        true: 'w-full',
      },
      loading: {
        true: 'cursor-wait opacity-80',
      },
    },
    defaultVariants: {
      variant: 'primary',
      size: 'md',
    },
  }
)

export interface ButtonProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement>,
    VariantProps<typeof buttonVariants> {
  isLoading?: boolean
  leftIcon?: React.ReactNode
  rightIcon?: React.ReactNode
  fullWidth?: boolean
}

const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      className,
      variant,
      size,
      fullWidth,
      isLoading,
      leftIcon,
      rightIcon,
      children,
      disabled,
      ...props
    },
    ref
  ) => {
    return (
      <button
        ref={ref}
        className={cn(buttonVariants({ variant, size, fullWidth, loading: isLoading, className }))}
        disabled={disabled ?? isLoading}
        aria-busy={isLoading}
        {...props}
      >
        {isLoading ? (
          <svg
            className="h-4 w-4 animate-spin"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            aria-hidden="true"
          >
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
        ) : (
          leftIcon
        )}
        {children}
        {!isLoading && rightIcon}
      </button>
    )
  }
)

Button.displayName = 'Button'
export { Button, buttonVariants }
