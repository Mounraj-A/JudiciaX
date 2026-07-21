// ─── Global Loading Infrastructure ────────────────────────────────────────────
import { useAppSelector } from '@/store'
import { selectGlobalLoading, selectLoadingMessage } from '@/store/slices/uiSlice'
import { LoadingSpinner } from '@/shared/design-system/components/feedback'
import { motion, AnimatePresence } from 'framer-motion'

// ─── Global Loader Overlay ────────────────────────────────────────────────────
/** Full-screen overlay triggered by `ui/setGlobalLoading` action. */
export function GlobalLoader() {
  const isLoading = useAppSelector(selectGlobalLoading)
  const message   = useAppSelector(selectLoadingMessage)

  return (
    <AnimatePresence>
      {isLoading && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          className="fixed inset-0 z-[var(--tc-z-overlay,9999)] flex flex-col items-center justify-center bg-black/40 backdrop-blur-xs"
          role="status"
          aria-live="assertive"
          aria-label={message ?? 'Processing application request'}
        >
          <div className="flex flex-col items-center gap-4 rounded-2xl bg-white p-8 shadow-overlay">
            <LoadingSpinner size="lg" color="#0F1D3A" />
            <p className="text-sm font-semibold text-[#111827]">
              {message ?? 'Please wait while processing...'}
            </p>
          </div>
        </motion.div>
      )}
    </AnimatePresence>
  )
}

// ─── Top Progress Loader ──────────────────────────────────────────────────────
/** Top bar progress indicator for page navigation and background requests. */
export function TopProgressBar({ isAnimating = false }: { isAnimating?: boolean }) {
  if (!isAnimating) return null

  return (
    <div className="fixed top-0 left-0 right-0 z-[9999] h-1 overflow-hidden bg-transparent">
      <motion.div
        initial={{ width: '0%' }}
        animate={{ width: '85%' }}
        transition={{ duration: 8, ease: 'easeOut' }}
        className="h-full bg-[#EA580C]"
      />
    </div>
  )
}
