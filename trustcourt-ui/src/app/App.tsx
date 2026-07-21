// ─── Application Root Component ───────────────────────────────────────────────
import { AppProviders } from '@/app/providers/AppProviders'
import { AppRouterProvider } from '@/app/router'

/**
 * TrustCourt Enterprise Application Root.
 * Composes the global provider tree around the central router.
 */
export function App() {
  return (
    <AppProviders>
      <AppRouterProvider />
    </AppProviders>
  )
}

export default App
