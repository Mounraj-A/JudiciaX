// ─── Session & Token Manager — Phase F2 ───────────────────────────────────────
// Extends Phase F1 foundation with:
// - StorageStrategy abstraction (Local / Session / Memory)
// - StorageStrategyFactory (selects based on rememberMe)
// - SessionMetadataCollector (lightweight, privacy-safe)
// - Completed IdleTimer TODO: dispatches logout on timeout
import { APP_CONSTANTS } from '@/constants'
import { logger }        from '@/core/logger'
import { eventBus }      from '@/core/events'
import type { AuthTokens } from '@/types/auth'

// ─── Storage Strategy Interface ───────────────────────────────────────────────
export interface StorageStrategy {
  get<T>(key: string): T | null
  set<T>(key: string, value: T): void
  remove(key: string): void
  clear(prefix?: string): void
}

// ─── Storage Implementations ──────────────────────────────────────────────────
class LocalStorageStrategy implements StorageStrategy {
  get<T>(key: string): T | null {
    try { const r = localStorage.getItem(key); return r ? (JSON.parse(r) as T) : null } catch { return null }
  }
  set<T>(key: string, value: T): void {
    try { localStorage.setItem(key, JSON.stringify(value)) } catch { /* quota */ }
  }
  remove(key: string): void { localStorage.removeItem(key) }
  clear(prefix?: string): void {
    if (!prefix) { localStorage.clear(); return }
    Object.keys(localStorage).filter((k) => k.startsWith(prefix)).forEach((k) => localStorage.removeItem(k))
  }
}

class SessionStorageStrategy implements StorageStrategy {
  get<T>(key: string): T | null {
    try { const r = sessionStorage.getItem(key); return r ? (JSON.parse(r) as T) : null } catch { return null }
  }
  set<T>(key: string, value: T): void {
    try { sessionStorage.setItem(key, JSON.stringify(value)) } catch { /* quota */ }
  }
  remove(key: string): void { sessionStorage.removeItem(key) }
  clear(prefix?: string): void {
    if (!prefix) { sessionStorage.clear(); return }
    Object.keys(sessionStorage).filter((k) => k.startsWith(prefix)).forEach((k) => sessionStorage.removeItem(k))
  }
}

class MemoryStorageStrategy implements StorageStrategy {
  private _store = new Map<string, string>()
  get<T>(key: string): T | null {
    try { const r = this._store.get(key); return r ? (JSON.parse(r) as T) : null } catch { return null }
  }
  set<T>(key: string, value: T): void { this._store.set(key, JSON.stringify(value)) }
  remove(key: string): void { this._store.delete(key) }
  clear(prefix?: string): void {
    if (!prefix) { this._store.clear(); return }
    for (const k of this._store.keys()) { if (k.startsWith(prefix)) this._store.delete(k) }
  }
}

// ─── Storage Strategy Factory ──────────────────────────────────────────────────
export class StorageStrategyFactory {
  static create(rememberMe: boolean): StorageStrategy {
    if (rememberMe) {
      logger.debug('[StorageStrategy] Using LocalStorage (rememberMe=true)')
      return new LocalStorageStrategy()
    }
    logger.debug('[StorageStrategy] Using SessionStorage (rememberMe=false)')
    return new SessionStorageStrategy()
  }

  /** Memory strategy for future ultra-secure environments (not activated in Phase F2). */
  static createMemory(): StorageStrategy {
    return new MemoryStorageStrategy()
  }
}

// ─── Storage Manager ──────────────────────────────────────────────────────────
class StorageManager {
  private _strategy: StorageStrategy = new LocalStorageStrategy()

  setStrategy(strategy: StorageStrategy): void {
    this._strategy = strategy
  }

  get<T>(key: string): T | null    { return this._strategy.get<T>(key) }
  set<T>(key: string, value: T): void { this._strategy.set(key, value) }
  remove(key: string): void        { this._strategy.remove(key) }
  clear(prefix?: string): void     { this._strategy.clear(prefix) }

  /** Also expose raw localStorage access for cross-storage reads on recovery. */
  session = {
    get:    <T>(key: string) => new SessionStorageStrategy().get<T>(key),
    set:    <T>(key: string, value: T) => new SessionStorageStrategy().set(key, value),
    remove: (key: string) => new SessionStorageStrategy().remove(key),
  }

  local = {
    get:    <T>(key: string) => new LocalStorageStrategy().get<T>(key),
    set:    <T>(key: string, value: T) => new LocalStorageStrategy().set(key, value),
    remove: (key: string) => new LocalStorageStrategy().remove(key),
  }
}

export const storage = new StorageManager()

// ─── Token Keys ───────────────────────────────────────────────────────────────
export const TOKEN_KEYS = {
  ACCESS:  `${APP_CONSTANTS.STORAGE_PREFIX}access_token`,
  REFRESH: `${APP_CONSTANTS.STORAGE_PREFIX}refresh_token`,
  EXPIRY:  `${APP_CONSTANTS.STORAGE_PREFIX}token_expiry`,
  USER:    `${APP_CONSTANTS.STORAGE_PREFIX}user`,
} as const

// ─── Token Manager ────────────────────────────────────────────────────────────
class TokenManager {
  saveTokens(tokens: AuthTokens): void {
    // Write to BOTH storages so session recovery can find them regardless
    // of which strategy is active. The active strategy is the write target;
    // localStorage is used as the fallback for recovery.
    storage.set(TOKEN_KEYS.ACCESS,  tokens.accessToken)
    storage.set(TOKEN_KEYS.REFRESH, tokens.refreshToken)
    storage.set(TOKEN_KEYS.EXPIRY,  Date.now() + tokens.expiresIn)
    logger.info('[TokenManager] Tokens saved')
  }

  getAccessToken(): string | null {
    // Check active strategy first, then localStorage fallback
    return storage.get<string>(TOKEN_KEYS.ACCESS)
      ?? storage.local.get<string>(TOKEN_KEYS.ACCESS)
      ?? storage.session.get<string>(TOKEN_KEYS.ACCESS)
  }

  getRefreshToken(): string | null {
    return storage.get<string>(TOKEN_KEYS.REFRESH)
      ?? storage.local.get<string>(TOKEN_KEYS.REFRESH)
      ?? storage.session.get<string>(TOKEN_KEYS.REFRESH)
  }

  isAccessTokenExpired(): boolean {
    const expiry = storage.get<number>(TOKEN_KEYS.EXPIRY)
      ?? storage.local.get<number>(TOKEN_KEYS.EXPIRY)
      ?? storage.session.get<number>(TOKEN_KEYS.EXPIRY)
    if (!expiry) return true
    return Date.now() >= expiry
  }

  clearTokens(): void {
    // Clear from all storage tiers
    Object.values(TOKEN_KEYS).forEach((key) => {
      storage.remove(key)
      storage.local.remove(key)
      storage.session.remove(key)
    })
    logger.info('[TokenManager] Tokens cleared from all storage tiers')
  }

  hasValidSession(): boolean {
    return !!this.getAccessToken() && !this.isAccessTokenExpired()
  }
}

export const tokenManager = new TokenManager()

// ─── Idle Timer ───────────────────────────────────────────────────────────────
class IdleTimer {
  private timer:     ReturnType<typeof setTimeout> | null = null
  private warnTimer: ReturnType<typeof setTimeout> | null = null
  private readonly timeout    = APP_CONSTANTS.IDLE_TIMEOUT
  private readonly warnBefore = 2 * 60 * 1000   // Warn 2 min before timeout
  private readonly events = ['mousedown', 'mousemove', 'keydown', 'scroll', 'touchstart']

  start(): void {
    this.events.forEach((e) => window.addEventListener(e, this.reset))
    this.reset()
    logger.info('[IdleTimer] Started')
  }

  stop(): void {
    this.events.forEach((e) => window.removeEventListener(e, this.reset))
    if (this.timer)     clearTimeout(this.timer)
    if (this.warnTimer) clearTimeout(this.warnTimer)
    this.timer     = null
    this.warnTimer = null
    logger.info('[IdleTimer] Stopped')
  }

  private reset = (): void => {
    if (this.timer)     clearTimeout(this.timer)
    if (this.warnTimer) clearTimeout(this.warnTimer)

    eventBus.emit('session:active')

    this.warnTimer = setTimeout(() => {
      eventBus.emit('session:idle-warning', { remainingMs: this.warnBefore })
      logger.warn('[IdleTimer] Idle warning emitted')
    }, this.timeout - this.warnBefore)

    this.timer = setTimeout(() => {
      eventBus.emit('session:idle-timeout')
      logger.warn('[IdleTimer] Idle timeout — session:idle-timeout emitted')
      // sessionService.init() wires this event to dispatch(markSessionExpired())
      // SessionContext then triggers authService.logout()
    }, this.timeout)
  }
}

export const idleTimer = new IdleTimer()
