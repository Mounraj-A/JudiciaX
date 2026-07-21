// ─── Global Error Boundary Infrastructure ─────────────────────────────────────
import { Component, type ErrorInfo, type ReactNode } from 'react'
import { ErrorState as DSErrorState } from '@/shared/design-system/components/feedback'
import { logger } from '@/core/logger'
import { eventBus } from '@/core/events'

interface Props {
  children: ReactNode
  fallback?: ReactNode
}

interface State {
  hasError: boolean
  error:    Error | null
}

export class ErrorBoundary extends Component<Props, State> {
  public state: State = {
    hasError: false,
    error:    null,
  }

  public static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error }
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    logger.error('[ErrorBoundary] Uncaught application exception', {
      error:     error.message,
      stack:     error.stack,
      componentStack: errorInfo.componentStack,
    })
    eventBus.emit('app:error', { error, info: errorInfo })
  }

  private handleReset = () => {
    this.setState({ hasError: false, error: null })
    window.location.reload()
  }

  public render() {
    if (this.state.hasError) {
      if (this.props.fallback) return this.props.fallback

      return (
        <div className="flex min-h-screen w-full items-center justify-center bg-[#F8F9FA] p-6">
          <div className="w-full max-w-md rounded-2xl border border-[#E5E7EB] bg-white p-8 shadow-large">
            <DSErrorState
              title="System Exception Encountered"
              description="An unexpected error occurred in the TrustCourt judicial interface. Our diagnostic logs have captured this incident."
              retry={this.handleReset}
            />
          </div>
        </div>
      )
    }

    return this.props.children
  }
}
