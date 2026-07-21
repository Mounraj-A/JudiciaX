import React from 'react'
import { cva, type VariantProps } from 'class-variance-authority'
import { cn } from '@/shared/design-system/utils/cn'

const badgeVariants = cva(
  'inline-flex items-center gap-1 rounded-full px-2.5 py-0.5 text-xs font-medium transition-colors',
  {
    variants: {
      variant: {
        default:     'bg-[#0F1D3A] text-white',
        secondary:   'bg-[#F3F4F6] text-[#374151]',
        success:     'bg-[#D1FAE5] text-[#065F46]',
        warning:     'bg-[#FEF3C7] text-[#92400E]',
        danger:      'bg-[#FEE2E2] text-[#991B1B]',
        info:        'bg-[#E0F2FE] text-[#075985]',
        outline:     'border border-current bg-transparent',
        // Priority variants
        emergency:   'bg-[#FEE2E2] text-[#B91C1C] border border-[#FECACA]',
        critical:    'bg-[#FFEDD5] text-[#9A3412] border border-[#FED7AA]',
        high:        'bg-[#FEF3C7] text-[#92400E] border border-[#FDE68A]',
        medium:      'bg-[#E0F2FE] text-[#075985] border border-[#BAE6FD]',
        low:         'bg-[#D1FAE5] text-[#065F46] border border-[#A7F3D0]',
        // AI Module
        'ai-jpi':       'bg-[#FFEDD5] text-[#EA580C]',
        'ai-cts':       'bg-[#CCFBF1] text-[#0F766E]',
        'ai-xai':       'bg-[#EDE9FE] text-[#6D28D9]',
        'ai-governance':'bg-[#F1F5F9] text-[#475569]',
      },
      size: {
        sm: 'px-2 py-0 text-[10px]',
        md: 'px-2.5 py-0.5 text-xs',
        lg: 'px-3 py-1 text-sm',
      },
    },
    defaultVariants: { variant: 'default', size: 'md' },
  }
)

export interface BadgeProps
  extends React.HTMLAttributes<HTMLSpanElement>,
    VariantProps<typeof badgeVariants> {
  dot?: boolean
}

function Badge({ className, variant, size, dot, children, ...props }: BadgeProps) {
  return (
    <span className={cn(badgeVariants({ variant, size, className }))} {...props}>
      {dot && (
        <span
          className="h-1.5 w-1.5 rounded-full bg-current opacity-80"
          aria-hidden="true"
        />
      )}
      {children}
    </span>
  )
}

export { Badge, badgeVariants }
