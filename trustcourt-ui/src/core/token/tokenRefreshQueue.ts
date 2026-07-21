// ─── Token Refresh Queue ──────────────────────────────────────────────────────
// Phase F2 – Concurrent refresh protection
//
// Problem: If 5 API calls return 401 simultaneously, we must NOT fire 5 refresh
// requests. We fire exactly ONE, then resolve all 5 pending callers with the
// new token.
//
// Solution: A singleton queue that:
//   1. Starts the refresh on the first 401
//   2. Enqueues subsequent callers as pending promises
//   3. Drains (resolves) all pending callers when refresh succeeds
//   4. Rejects all pending callers if refresh fails

import { logger } from '@/core/logger'

type Resolver = (token: string) => void
type Rejector = (error: unknown) => void

class RefreshQueueManager {
  private _isRefreshing  = false
  private _resolvers: Resolver[] = []
  private _rejectors: Rejector[] = []

  get isRefreshing(): boolean {
    return this._isRefreshing
  }

  /**
   * Enqueue a caller waiting for a fresh access token.
   *
   * If a refresh is already in progress, returns a promise that resolves
   * when the in-flight refresh completes.
   *
   * If no refresh is in progress, calls `refreshFn` to start one and
   * returns the resulting token.
   *
   * @param refreshFn - Async function that performs the token refresh
   */
  async enqueue(refreshFn: () => Promise<string>): Promise<string> {
    if (this._isRefreshing) {
      logger.debug('[RefreshQueue] Queuing caller — refresh in flight')
      return new Promise<string>((resolve, reject) => {
        this._resolvers.push(resolve)
        this._rejectors.push(reject)
      })
    }

    this._isRefreshing = true
    logger.info('[RefreshQueue] Starting token refresh')

    try {
      const newToken = await refreshFn()
      this._drain(newToken)
      return newToken
    } catch (error) {
      this._reject(error)
      throw error
    } finally {
      this._isRefreshing = false
    }
  }

  /** Resolve all queued callers with the new token. */
  private _drain(newToken: string): void {
    logger.debug(`[RefreshQueue] Draining ${this._resolvers.length} queued caller(s)`)
    this._resolvers.forEach((resolve) => resolve(newToken))
    this._resolvers = []
    this._rejectors = []
  }

  /** Reject all queued callers — refresh failed. */
  private _reject(error: unknown): void {
    logger.error(`[RefreshQueue] Rejecting ${this._rejectors.length} queued caller(s)`)
    this._rejectors.forEach((reject) => reject(error))
    this._resolvers = []
    this._rejectors = []
  }

  /** Reset queue state — use only in tests or after forced logout. */
  reset(): void {
    this._isRefreshing = false
    this._resolvers    = []
    this._rejectors    = []
  }
}

/** Singleton refresh queue — shared across all Axios interceptors. */
export const refreshQueue = new RefreshQueueManager()
