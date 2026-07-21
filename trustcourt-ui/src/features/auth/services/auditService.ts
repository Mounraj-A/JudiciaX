// ─── Audit Service ────────────────────────────────────────────────────────────
// Phase F2 – Frontend-side audit event hooks
//
// Emits structured audit events via the event bus.
// Phase F5+ will add a monitoring service transport (Datadog, etc.)
// For now: dev console logging + event bus only.
import type { AuditEventType, FrontendAuditEvent, AuditContext } from '@/types/auth/audit'
import { eventBus } from '@/core/events'
import { logger }   from '@/core/logger'
import { v4 as uuid } from 'uuid'

class AuditServiceClass {
  private buildContext(partial: Partial<AuditContext> = {}): AuditContext {
    return {
      timestamp:     new Date().toISOString(),
      correlationId: uuid(),
      path:          window.location.pathname,
      ...partial,
    }
  }

  emit(event: FrontendAuditEvent): void {
    logger.info(`[Audit] ${event.type}`, { context: event.context, details: event.details })
    // Map to event bus audit events
    const busEventMap: Partial<Record<AuditEventType, string>> = {
      LOGIN:                  'audit:login',
      LOGOUT:                 'audit:logout',
      TOKEN_REFRESH:          'audit:token-refresh',
      SESSION_EXPIRED:        'audit:session-expired',
      PERMISSION_DENIED:      'audit:permission-denied',
      UNAUTHORIZED_ACCESS:    'audit:unauthorized-access',
      REGISTRATION:           'audit:registration',
      PASSWORD_RESET_REQUEST: 'audit:password-reset',
    }
    const busEvent = busEventMap[event.type]
    if (busEvent) {
      // @ts-expect-error — dynamic event type mapping
      eventBus.emit(busEvent, event)
    }
  }

  onLogin(userId: string, role: string): void {
    this.emit({
      type:    'LOGIN',
      context: this.buildContext({ userId, role }),
    })
  }

  onLogout(userId: string): void {
    this.emit({
      type:    'LOGOUT',
      context: this.buildContext({ userId }),
    })
  }

  onTokenRefresh(): void {
    this.emit({
      type:    'TOKEN_REFRESH',
      context: this.buildContext(),
    })
  }

  onSessionExpired(userId?: string): void {
    this.emit({
      type:    'SESSION_EXPIRED',
      context: this.buildContext({ userId }),
    })
  }

  onPermissionDenied(permission: string, userId?: string): void {
    this.emit({
      type:    'PERMISSION_DENIED',
      context: this.buildContext({ userId }),
      details: { permission, path: window.location.pathname },
    })
  }

  onUnauthorizedAccess(path: string): void {
    this.emit({
      type:    'UNAUTHORIZED_ACCESS',
      context: this.buildContext(),
      details: { attemptedPath: path },
    })
  }

  onRegistration(email: string): void {
    this.emit({
      type:    'REGISTRATION',
      context: this.buildContext(),
      details: { email },
    })
  }
}

export const auditService = new AuditServiceClass()
