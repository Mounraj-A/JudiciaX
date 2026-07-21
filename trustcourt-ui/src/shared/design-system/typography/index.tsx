import React from 'react'
import { cn } from '@/shared/design-system/utils/cn'

// ─── Typography components ────────────────────────────────────────────────────
interface TextProps extends React.HTMLAttributes<HTMLElement> {
  as?: React.ElementType
  className?: string
  children: React.ReactNode
  muted?: boolean
  truncate?: boolean
}

function Display({ as: Tag = 'h1', className, children, ...props }: TextProps) {
  return (
    <Tag
      className={cn('text-4xl font-bold tracking-tight text-[#111827] text-balance', className)}
      {...props}
    >
      {children}
    </Tag>
  )
}

function Heading({ as: Tag = 'h2', className, children, ...props }: TextProps) {
  return (
    <Tag
      className={cn('text-2xl font-semibold tracking-tight text-[#111827]', className)}
      {...props}
    >
      {children}
    </Tag>
  )
}

function Title({ as: Tag = 'h3', className, children, ...props }: TextProps) {
  return (
    <Tag
      className={cn('text-xl font-semibold text-[#111827]', className)}
      {...props}
    >
      {children}
    </Tag>
  )
}

function Subtitle({ as: Tag = 'h4', className, children, muted, ...props }: TextProps) {
  return (
    <Tag
      className={cn('text-lg font-medium', muted ? 'text-[#6B7280]' : 'text-[#374151]', className)}
      {...props}
    >
      {children}
    </Tag>
  )
}

function Body({ as: Tag = 'p', className, children, muted, ...props }: TextProps) {
  return (
    <Tag
      className={cn('text-sm leading-relaxed', muted ? 'text-[#6B7280]' : 'text-[#374151]', className)}
      {...props}
    >
      {children}
    </Tag>
  )
}

function Caption({ as: Tag = 'p', className, children, muted = true, ...props }: TextProps) {
  return (
    <Tag
      className={cn('text-xs leading-snug', muted ? 'text-[#9CA3AF]' : 'text-[#6B7280]', className)}
      {...props}
    >
      {children}
    </Tag>
  )
}

function Label({ as: Tag = 'span', className, children, ...props }: TextProps) {
  return (
    <Tag
      className={cn('text-xs font-medium uppercase tracking-widest text-[#6B7280]', className)}
      {...props}
    >
      {children}
    </Tag>
  )
}

function Code({ className, children, ...props }: TextProps) {
  return (
    <code
      className={cn(
        'rounded bg-[#F3F4F6] px-1.5 py-0.5 text-sm font-mono text-[#374151] border border-[#E5E7EB]',
        className
      )}
      {...props}
    >
      {children}
    </code>
  )
}

export { Display, Heading, Title, Subtitle, Body, Caption, Label, Code }
