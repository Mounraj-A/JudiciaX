import React from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { cn } from '@/shared/design-system/utils/cn'

// ─── Motion Variants ──────────────────────────────────────────────────────────
export const motionVariants = {
  fadeIn: {
    initial: { opacity: 0 },
    animate: { opacity: 1, transition: { duration: 0.2 } },
    exit:    { opacity: 0, transition: { duration: 0.15 } },
  },
  slideUp: {
    initial: { opacity: 0, y: 8 },
    animate: { opacity: 1, y: 0, transition: { duration: 0.2, ease: [0.4, 0, 0.2, 1] as const } },
    exit:    { opacity: 0, y: 4,  transition: { duration: 0.15 } },
  },
  scaleIn: {
    initial: { opacity: 0, scale: 0.96 },
    animate: { opacity: 1, scale: 1,    transition: { duration: 0.15, ease: [0.4, 0, 0.2, 1] as const } },
    exit:    { opacity: 0, scale: 0.98, transition: { duration: 0.1 } },
  },
  slideFromRight: {
    initial: { opacity: 0, x: 40 },
    animate: { opacity: 1, x: 0,  transition: { duration: 0.25, ease: [0.4, 0, 0.2, 1] as const } },
    exit:    { opacity: 0, x: 20, transition: { duration: 0.2 } },
  },
}

// ─── Dialog ───────────────────────────────────────────────────────────────────
interface DialogProps {
  open: boolean
  onClose: () => void
  title?: string
  description?: string
  children: React.ReactNode
  size?: 'sm' | 'md' | 'lg' | 'xl' | 'full'
  footer?: React.ReactNode
}

const dialogSizes = {
  sm:   'max-w-sm',
  md:   'max-w-lg',
  lg:   'max-w-2xl',
  xl:   'max-w-4xl',
  full: 'max-w-[90vw]',
}

function Dialog({ open, onClose, title, description, children, size = 'md', footer }: DialogProps) {
  React.useEffect(() => {
    if (!open) return
    const handleKey = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose() }
    document.addEventListener('keydown', handleKey)
    return () => document.removeEventListener('keydown', handleKey)
  }, [open, onClose])

  return (
    <AnimatePresence>
      {open && (
        <>
          <motion.div
            {...motionVariants.fadeIn}
            className="fixed inset-0 bg-black/40 backdrop-blur-sm z-[var(--tc-z-overlay,300)]"
            onClick={onClose}
            aria-hidden="true"
          />
          <div
            role="dialog"
            aria-modal="true"
            aria-labelledby={title ? 'dialog-title' : undefined}
            aria-describedby={description ? 'dialog-description' : undefined}
            className="fixed inset-0 z-[var(--tc-z-modal,400)] flex items-center justify-center p-4"
          >
            <motion.div
              {...motionVariants.scaleIn}
              className={cn(
                'relative w-full bg-white rounded-xl shadow-overlay',
                dialogSizes[size]
              )}
            >
              {(title ?? description) && (
                <div className="px-6 pt-6 pb-4 border-b border-[#F3F4F6]">
                  {title && (
                    <h2 id="dialog-title" className="text-lg font-semibold text-[#111827]">
                      {title}
                    </h2>
                  )}
                  {description && (
                    <p id="dialog-description" className="text-sm text-[#6B7280] mt-1">
                      {description}
                    </p>
                  )}
                </div>
              )}
              <div className="p-6">{children}</div>
              {footer && (
                <div className="px-6 pb-6 pt-4 border-t border-[#F3F4F6] flex justify-end gap-3">
                  {footer}
                </div>
              )}
              <button
                onClick={onClose}
                className="absolute top-4 right-4 h-8 w-8 flex items-center justify-center rounded-lg text-[#9CA3AF] hover:bg-[#F3F4F6] hover:text-[#374151] transition-colors"
                aria-label="Close dialog"
              >
                ✕
              </button>
            </motion.div>
          </div>
        </>
      )}
    </AnimatePresence>
  )
}

// ─── Drawer ───────────────────────────────────────────────────────────────────
interface DrawerProps {
  open: boolean
  onClose: () => void
  title?: string
  children: React.ReactNode
  side?: 'left' | 'right'
  width?: string
}

function Drawer({ open, onClose, title, children, side = 'right', width = '28rem' }: DrawerProps) {
  React.useEffect(() => {
    if (!open) return
    const handleKey = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose() }
    document.addEventListener('keydown', handleKey)
    return () => document.removeEventListener('keydown', handleKey)
  }, [open, onClose])

  const slideVariant = {
    initial: { x: side === 'right' ? '100%' : '-100%' },
    animate: { x: 0, transition: { duration: 0.25, ease: [0.4, 0, 0.2, 1] as const } },
    exit:    { x: side === 'right' ? '100%' : '-100%', transition: { duration: 0.2 } },
  }

  return (
    <AnimatePresence>
      {open && (
        <>
          <motion.div
            {...motionVariants.fadeIn}
            className="fixed inset-0 bg-black/40 z-[300]"
            onClick={onClose}
            aria-hidden="true"
          />
          <motion.aside
            {...slideVariant}
            role="complementary"
            aria-label={title ?? 'Drawer'}
            className={cn(
              'fixed top-0 bottom-0 bg-white shadow-overlay z-[400] flex flex-col',
              side === 'right' ? 'right-0' : 'left-0'
            )}
            style={{ width }}
          >
            <div className="flex items-center justify-between px-6 py-4 border-b border-[#F3F4F6]">
              {title && <h2 className="text-lg font-semibold text-[#111827]">{title}</h2>}
              <button
                onClick={onClose}
                className="h-8 w-8 flex items-center justify-center rounded-lg text-[#9CA3AF] hover:bg-[#F3F4F6] transition-colors"
                aria-label="Close drawer"
              >
                ✕
              </button>
            </div>
            <div className="flex-1 overflow-y-auto p-6">{children}</div>
          </motion.aside>
        </>
      )}
    </AnimatePresence>
  )
}

export { Dialog, Drawer }
