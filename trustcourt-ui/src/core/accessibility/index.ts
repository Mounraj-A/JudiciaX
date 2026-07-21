// ─── Accessibility Management Layer ───────────────────────────────────────────
import { logger } from '@/core/logger'

/**
 * Accessibility Manager enforcing focus entrapment, keyboard shortcuts, and screen reader announcements.
 */
export class FocusManager {
  private static previousFocus: HTMLElement | null = null

  static saveFocus(): void {
    this.previousFocus = document.activeElement as HTMLElement
  }

  static restoreFocus(): void {
    if (this.previousFocus && typeof this.previousFocus.focus === 'function') {
      this.previousFocus.focus()
      this.previousFocus = null
    }
  }

  static trapFocus(container: HTMLElement, e: KeyboardEvent): void {
    if (e.key !== 'Tab') return

    const focusable = container.querySelectorAll<HTMLElement>(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
    )
    if (focusable.length === 0) return

    const first = focusable[0]
    const last  = focusable[focusable.length - 1]

    if (e.shiftKey && document.activeElement === first) {
      last.focus()
      e.preventDefault()
    } else if (!e.shiftKey && document.activeElement === last) {
      first.focus()
      e.preventDefault()
    }
  }
}

/**
 * Screen Reader Live Announcer for dynamic page updates and AI analysis results.
 */
export class ScreenReaderAnnouncer {
  private static liveRegion: HTMLElement | null = null

  private static getRegion(): HTMLElement {
    if (!this.liveRegion) {
      const el = document.createElement('div')
      el.setAttribute('aria-live', 'polite')
      el.setAttribute('aria-atomic', 'true')
      el.className = 'sr-only'
      document.body.appendChild(el)
      this.liveRegion = el
    }
    return this.liveRegion
  }

  static announce(message: string, priority: 'polite' | 'assertive' = 'polite'): void {
    const el = this.getRegion()
    el.setAttribute('aria-live', priority)
    el.textContent = message
    logger.debug(`[A11y Announce] (${priority}) ${message}`)
  }
}

/**
 * Checks system preferences for reduced motion.
 */
export function prefersReducedMotion(): boolean {
  return window.matchMedia('(prefers-reduced-motion: reduce)').matches
}
