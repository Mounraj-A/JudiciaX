import React from 'react'
import { cn } from '@/shared/design-system/utils/cn'

// ─── Skeleton ─────────────────────────────────────────────────────────────────
interface SkeletonProps extends React.HTMLAttributes<HTMLDivElement> {
  variant?: 'text' | 'circular' | 'rectangular'
  width?: string | number
  height?: string | number
}

function Skeleton({ className, variant = 'rectangular', width, height, style, ...props }: SkeletonProps) {
  const variantClass = {
    text: 'rounded',
    circular: 'rounded-full',
    rectangular: 'rounded-md',
  }[variant]

  return (
    <div
      className={cn(
        'bg-[#E5E7EB] animate-[shimmer_2s_linear_infinite]',
        'bg-gradient-to-r from-[#E5E7EB] via-[#F3F4F6] to-[#E5E7EB]',
        'bg-[length:200%_100%]',
        variantClass,
        className
      )}
      style={{ width, height, ...style }}
      aria-hidden="true"
      role="status"
      {...props}
    />
  )
}

// ─── Progress ─────────────────────────────────────────────────────────────────
interface ProgressProps {
  value?: number
  max?: number
  label?: string
  color?: 'primary' | 'success' | 'warning' | 'danger' | 'ai-jpi' | 'ai-cts'
  size?: 'sm' | 'md' | 'lg'
  showValue?: boolean
}

const progressColors = {
  primary:  'bg-[#0F1D3A]',
  success:  'bg-[#059669]',
  warning:  'bg-[#D97706]',
  danger:   'bg-[#B91C1C]',
  'ai-jpi': 'bg-[#EA580C]',
  'ai-cts': 'bg-[#0F766E]',
}

function Progress({ value = 0, max = 100, label, color = 'primary', size = 'md', showValue }: ProgressProps) {
  const pct = Math.min(100, Math.max(0, (value / max) * 100))
  const trackHeight = { sm: 'h-1', md: 'h-2', lg: 'h-3' }[size]

  return (
    <div className="w-full space-y-1">
      {(label ?? showValue) && (
        <div className="flex justify-between text-xs text-[#6B7280]">
          {label && <span>{label}</span>}
          {showValue && <span>{Math.round(pct)}%</span>}
        </div>
      )}
      <div
        className={cn('w-full rounded-full bg-[#E5E7EB] overflow-hidden', trackHeight)}
        role="progressbar"
        aria-valuenow={value}
        aria-valuemin={0}
        aria-valuemax={max}
        aria-label={label}
      >
        <div
          className={cn('h-full rounded-full transition-all duration-500 ease-out', progressColors[color])}
          style={{ width: `${pct}%` }}
        />
      </div>
    </div>
  )
}

// ─── LoadingSpinner ───────────────────────────────────────────────────────────
interface SpinnerProps {
  size?: 'sm' | 'md' | 'lg'
  color?: string
  label?: string
}

function LoadingSpinner({ size = 'md', color = '#0F1D3A', label = 'Loading...' }: SpinnerProps) {
  const sizeClass = { sm: 'h-4 w-4', md: 'h-8 w-8', lg: 'h-12 w-12' }[size]

  return (
    <div role="status" aria-label={label} className="flex items-center justify-center">
      <svg
        className={cn('animate-spin', sizeClass)}
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
        aria-hidden="true"
      >
        <circle className="opacity-25" cx="12" cy="12" r="10" stroke={color} strokeWidth="4" />
        <path className="opacity-75" fill={color} d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
      </svg>
      <span className="sr-only">{label}</span>
    </div>
  )
}

// ─── EmptyState ───────────────────────────────────────────────────────────────
interface EmptyStateProps {
  title: string
  description?: string
  icon?: React.ReactNode
  action?: React.ReactNode
}

function EmptyState({ title, description, icon, action }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center py-16 px-8 text-center space-y-4">
      {icon && (
        <div className="h-16 w-16 rounded-2xl bg-[#F3F4F6] flex items-center justify-center text-[#9CA3AF]">
          {icon}
        </div>
      )}
      <div className="space-y-1">
        <h3 className="text-lg font-semibold text-[#111827]">{title}</h3>
        {description && <p className="text-sm text-[#6B7280] max-w-sm">{description}</p>}
      </div>
      {action && <div className="mt-2">{action}</div>}
    </div>
  )
}

// ─── ErrorState ───────────────────────────────────────────────────────────────
interface ErrorStateProps {
  title?: string
  description?: string
  retry?: () => void
}

function ErrorState({
  title = 'Something went wrong',
  description = 'An unexpected error occurred. Please try again.',
  retry,
}: ErrorStateProps) {
  return (
    <div
      className="flex flex-col items-center justify-center py-16 px-8 text-center space-y-4"
      role="alert"
    >
      <div className="h-16 w-16 rounded-2xl bg-[#FEE2E2] flex items-center justify-center">
        <svg className="h-8 w-8 text-[#B91C1C]" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
      </div>
      <div className="space-y-1">
        <h3 className="text-lg font-semibold text-[#111827]">{title}</h3>
        <p className="text-sm text-[#6B7280] max-w-sm">{description}</p>
      </div>
      {retry && (
        <button
          onClick={retry}
          className="text-sm font-medium text-[#1E3A8A] hover:underline"
        >
          Try again
        </button>
      )}
    </div>
  )
}

// ─── Alert ────────────────────────────────────────────────────────────────────
type AlertVariant = 'info' | 'success' | 'warning' | 'danger'

interface AlertProps {
  variant?: AlertVariant
  title?: string
  children: React.ReactNode
  onDismiss?: () => void
}

const alertStyles: Record<AlertVariant, { container: string; icon: string; iconColor: string }> = {
  info:    { container: 'bg-[#EFF6FF] border-[#BFDBFE] text-[#1E40AF]', icon: 'ℹ', iconColor: 'text-[#3B82F6]' },
  success: { container: 'bg-[#F0FDF4] border-[#BBF7D0] text-[#166534]', icon: '✓', iconColor: 'text-[#16A34A]' },
  warning: { container: 'bg-[#FFFBEB] border-[#FDE68A] text-[#92400E]', icon: '⚠', iconColor: 'text-[#D97706]' },
  danger:  { container: 'bg-[#FEF2F2] border-[#FECACA] text-[#991B1B]', icon: '✕', iconColor: 'text-[#DC2626]' },
}

function Alert({ variant = 'info', title, children, onDismiss }: AlertProps) {
  const styles = alertStyles[variant]
  return (
    <div
      role="alert"
      className={cn('flex gap-3 rounded-lg border p-4', styles.container)}
    >
      <span className={cn('mt-0.5 text-sm font-bold', styles.iconColor)} aria-hidden="true">
        {styles.icon}
      </span>
      <div className="flex-1 space-y-1">
        {title && <p className="text-sm font-semibold">{title}</p>}
        <div className="text-sm opacity-90">{children}</div>
      </div>
      {onDismiss && (
        <button
          onClick={onDismiss}
          className="ml-auto text-sm opacity-50 hover:opacity-100 transition-opacity"
          aria-label="Dismiss alert"
        >
          ✕
        </button>
      )}
    </div>
  )
}

export { Skeleton, Progress, LoadingSpinner, EmptyState, ErrorState, Alert }
