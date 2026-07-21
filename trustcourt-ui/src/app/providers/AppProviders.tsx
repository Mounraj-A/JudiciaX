// ─── Composed Provider Tree Architecture ──────────────────────────────────────
import React from 'react'
import { Provider as ReduxProvider } from 'react-redux'
import { QueryClientProvider } from '@tanstack/react-query'
import { ThemeProvider } from '@/shared/design-system/theme/ThemeProvider'
import { BreakpointProvider } from '@/core/responsive'
import { store } from '@/store'
import { queryClient } from '@/lib/queryClient'
import { ErrorBoundary } from '@/app/ErrorBoundary'
import { AppInitializer } from '@/app/initializer/AppInitializer'
import { GlobalLoader } from '@/core/loading'
import { ToastManager } from '@/core/notifications'
import { AuthProviderTree } from '@/features/auth/providers/AuthProviderTree'
import { SilentRefreshIndicator } from '@/features/auth/loading/SilentRefreshIndicator'
import { IdleWarningDialogGuarded } from '@/features/auth/components/IdleWarningDialog'
import { SessionStatusBar } from '@/features/auth/components/SessionStatusBar'

interface AppProvidersProps {
  children: React.ReactNode
}

/**
 * Enterprise Provider Tree — Phase F2 composition hierarchy:
 * ErrorBoundary → Redux → TanStackQuery → Theme → Breakpoint
 *   → Initializer → AuthProviderTree → (Children + Overlays)
 *
 * AuthProviderTree contains: Auth → Session → Identity → Authorization → Permission
 */
export function AppProviders({ children }: AppProvidersProps) {
  return (
    <ErrorBoundary>
      <ReduxProvider store={store}>
        <QueryClientProvider client={queryClient}>
          <ThemeProvider defaultTheme="light" defaultDensity="default">
            <BreakpointProvider>
              <AppInitializer>
                <AuthProviderTree>
                  {children}
                  <GlobalLoader />
                  <ToastManager />
                  <SilentRefreshIndicator />
                  <IdleWarningDialogGuarded />
                  <SessionStatusBar />
                </AuthProviderTree>
              </AppInitializer>
            </BreakpointProvider>
          </ThemeProvider>
        </QueryClientProvider>
      </ReduxProvider>
    </ErrorBoundary>
  )
}
