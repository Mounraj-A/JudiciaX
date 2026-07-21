// ─── Dynamic Refresh Timer ────────────────────────────────────────────────────
// Phase F2 – Dynamic token refresh scheduling
//
// Replaces the static APP_CONSTANTS.TOKEN_REFRESH = 5 * 60 * 1000 constant.
//
// Strategy: Refresh at 85% of token lifetime elapsed.
//   - Token valid 3600s (1h)  → refresh fires at 3060s (51 min)
//   - Token valid 900s  (15m) → refresh fires at 765s  (12.75 min)
//   - Token valid 300s  (5m)  → refresh fires at 255s  (4.25 min)
//
// This is safer than a fixed window because it adapts to any expiresIn
// value the Spring Boot backend sends.

import { logger } from '@/core/logger'

/** Refresh threshold — fire refresh when this fraction of lifetime has elapsed. */
const REFRESH_THRESHOLD = 0.85

/**
 * Calculate when to schedule the refresh.
 *
 * @param expiresInMs - Token lifetime in milliseconds (from backend `expiresIn`)
 * @param threshold   - Fraction of lifetime at which to refresh (default 0.85)
 * @returns Delay in ms before refresh should fire
 */
export function calculateRefreshDelay(
  expiresInMs: number,
  threshold = REFRESH_THRESHOLD
): number {
  if (expiresInMs <= 0) return 0
  const delay = Math.floor(expiresInMs * threshold)
  logger.debug(`[DynamicRefreshTimer] expiresIn=${expiresInMs}ms → delay=${delay}ms (${threshold * 100}%)`)
  return delay
}

// ─── Dynamic Refresh Timer Class ──────────────────────────────────────────────
export class DynamicRefreshTimer {
  private _timer:     ReturnType<typeof setTimeout> | null = null
  private _callback:  (() => void) | null = null

  /**
   * Schedule a refresh callback.
   *
   * @param expiresInMs - Token lifetime in ms from backend response
   * @param callback    - Function to call when timer fires (typically tokenService.refresh)
   */
  schedule(expiresInMs: number, callback: () => void): void {
    this.cancel()
    this._callback = callback

    const delay = calculateRefreshDelay(expiresInMs)
    if (delay <= 0) {
      logger.warn('[DynamicRefreshTimer] Token already near expiry — refreshing immediately')
      callback()
      return
    }

    this._timer = setTimeout(() => {
      logger.info(`[DynamicRefreshTimer] Firing refresh at ${REFRESH_THRESHOLD * 100}% of lifetime`)
      callback()
    }, delay)
  }

  /** Reschedule with a new expiresIn value (called after successful refresh). */
  reschedule(expiresInMs: number): void {
    if (this._callback) {
      this.schedule(expiresInMs, this._callback)
    }
  }

  /** Cancel any pending refresh timer. */
  cancel(): void {
    if (this._timer !== null) {
      clearTimeout(this._timer)
      this._timer = null
      logger.debug('[DynamicRefreshTimer] Timer cancelled')
    }
  }

  get isScheduled(): boolean {
    return this._timer !== null
  }
}

/** Singleton dynamic refresh timer — one per session. */
export const dynamicRefreshTimer = new DynamicRefreshTimer()
