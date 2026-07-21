// ─── Application Loader Component ─────────────────────────────────────────────
import { LoadingSpinner } from '@/shared/design-system/components/feedback'

/**
 * Full-screen loading screen shown during initial session restoration
 * or platform bootstrapping.
 */
export function AppLoader({ message = 'Initializing TrustCourt Enterprise Foundation...' }: { message?: string }) {
  return (
    <div
      className="flex h-screen w-screen flex-col items-center justify-center bg-[#0F1D3A] text-white"
      role="status"
      aria-label={message}
    >
      <div className="flex flex-col items-center gap-6 text-center">
        <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-[#C2410C] text-2xl font-bold shadow-large">
          TC
        </div>
        <div className="space-y-2">
          <h1 className="text-xl font-bold tracking-tight text-white">TrustCourt</h1>
          <p className="text-xs text-white/70">{message}</p>
        </div>
        <LoadingSpinner size="md" color="#FFFFFF" label={message} />
      </div>
    </div>
  )
}
