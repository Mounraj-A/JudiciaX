// â”€â”€â”€ ErrorBoundary â€” Phase F3 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import { Component, ErrorInfo, ReactNode } from 'react'
import { ErrorLayout } from '../layout/StateLayouts'

interface Props {
  children: ReactNode
  fallback?: ReactNode
  onError?: (error: Error, errorInfo: ErrorInfo) => void
}

interface State {
  hasError: boolean
  error: Error | null
}

export class ErrorBoundary extends Component<Props, State> {
  public state: State = {
    hasError: false,
    error: null
  }

  public static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error }
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('Uncaught error:', error, errorInfo)
    this.props.onError?.(error, errorInfo)
  }

  public render() {
    if (this.state.hasError) {
      if (this.props.fallback) return this.props.fallback
      return (
        <ErrorLayout
          title="Component Error"
          message={this.state.error?.message}
          action={
            <button
              onClick={() => this.setState({ hasError: false, error: null })}
              style={{
                background: '#0F1D3A', color: '#fff', border: 'none',
                padding: '0.5rem 1rem', borderRadius: '0.375rem', cursor: 'pointer',
                fontSize: '0.875rem'
              }}
            >
              Try Again
            </button>
          }
        />
      )
    }

    return this.props.children
  }
}
