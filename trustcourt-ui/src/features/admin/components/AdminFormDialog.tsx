// ─── Admin Form Dialog ────────────────────────────────────────────────────────
// Reusable enterprise form modal for all admin CRUD operations
import React, { type ReactNode } from 'react'
import { Modal } from '@/shared/components/overlay/Modal'
import { Button } from '@/shared/design-system/components/primitives/Button'

interface AdminFormDialogProps {
  title: string
  isOpen: boolean
  onClose: () => void
  onSubmit: () => void
  isLoading?: boolean
  isDestructive?: boolean
  submitLabel?: string
  cancelLabel?: string
  children: ReactNode
  error?: string | null
  size?: 'sm' | 'md' | 'lg'
}

export function AdminFormDialog({
  title, isOpen, onClose, onSubmit, isLoading = false,
  isDestructive = false, submitLabel = 'Save', cancelLabel = 'Cancel',
  children, error, size = 'md'
}: AdminFormDialogProps) {
  const widths = { sm: 400, md: 560, lg: 720 }
  return (
    <Modal isOpen={isOpen} onClose={onClose} title={title}>
      <div style={{ width: widths[size], maxWidth: '90vw' }}>
        <div style={{ padding: '1.25rem 1.5rem', minHeight: 80 }}>
          {children}
          {error && (
            <div style={{
              marginTop: '0.75rem', padding: '0.75rem 1rem',
              background: '#FEF2F2', border: '1px solid #FCA5A5',
              borderRadius: '0.375rem', fontSize: '0.875rem', color: '#DC2626'
            }}>
              {error}
            </div>
          )}
        </div>
        <div style={{
          display: 'flex', justifyContent: 'flex-end', gap: '0.75rem',
          padding: '1rem 1.5rem',
          borderTop: '1px solid #E5E7EB', background: '#F9FAFB',
          borderRadius: '0 0 0.75rem 0.75rem'
        }}>
          <Button variant="ghost" onClick={onClose} disabled={isLoading}>
            {cancelLabel}
          </Button>
          <Button
            variant={isDestructive ? 'destructive' : 'primary'}
            onClick={onSubmit}
            disabled={isLoading}
          >
            {isLoading ? (
              <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" style={{ animation: 'spin 1s linear infinite' }}>
                  <path d="M21 12a9 9 0 1 1-6.219-8.56" />
                </svg>
                Processing…
              </span>
            ) : submitLabel}
          </Button>
        </div>
      </div>
      <style>{`@keyframes spin { to { transform: rotate(360deg) } }`}</style>
    </Modal>
  )
}

// ── Confirm Dialog ─────────────────────────────────────────────────────────────
interface ConfirmDialogProps {
  isOpen: boolean
  onClose: () => void
  onConfirm: () => void
  title: string
  description: string
  confirmLabel?: string
  isLoading?: boolean
  variant?: 'destructive' | 'warning'
}

export function ConfirmDialog({
  isOpen, onClose, onConfirm, title, description,
  confirmLabel = 'Confirm', isLoading = false, variant = 'destructive'
}: ConfirmDialogProps) {
  return (
    <Modal isOpen={isOpen} onClose={onClose} title={title}>
      <div style={{ padding: '1.25rem 1.5rem', width: 420, maxWidth: '90vw' }}>
        <p style={{ fontSize: '0.9375rem', color: '#4B5563', lineHeight: 1.6 }}>{description}</p>
        <div style={{
          display: 'flex', justifyContent: 'flex-end', gap: '0.75rem',
          marginTop: '1.5rem', paddingTop: '1rem', borderTop: '1px solid #E5E7EB'
        }}>
          <Button variant="ghost" onClick={onClose} disabled={isLoading}>Cancel</Button>
          <Button variant={variant === 'destructive' ? 'destructive' : 'primary'} onClick={onConfirm} disabled={isLoading}>
            {isLoading ? 'Processing…' : confirmLabel}
          </Button>
        </div>
      </div>
    </Modal>
  )
}
