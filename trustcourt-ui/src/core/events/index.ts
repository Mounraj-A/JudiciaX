// ─── Global Event Bus ─────────────────────────────────────────────────────────
import type { AppEvent, AppEventType, EventHandler } from '@/types/events'
import { logger } from '@/core/logger'

class EventBus {
  private readonly listeners = new Map<AppEventType, Set<EventHandler>>()

  /** Subscribe to an event. Returns an unsubscribe function. */
  on<T = unknown>(type: AppEventType, handler: EventHandler<T>): () => void {
    if (!this.listeners.has(type)) {
      this.listeners.set(type, new Set())
    }
    this.listeners.get(type)!.add(handler as EventHandler)
    logger.debug(`[EventBus] Subscribed: ${type}`)

    return () => this.off(type, handler as EventHandler)
  }

  /** Unsubscribe from an event. */
  off(type: AppEventType, handler: EventHandler): void {
    this.listeners.get(type)?.delete(handler)
  }

  /** Emit an event to all subscribers. */
  emit<T = unknown>(type: AppEventType, payload?: T): void {
    const event: AppEvent<T> = { type, payload, timestamp: Date.now() }
    logger.debug(`[EventBus] Emit: ${type}`, payload)

    this.listeners.get(type)?.forEach((handler) => {
      try {
        handler(event as AppEvent)
      } catch (err) {
        logger.error(`[EventBus] Handler error on ${type}`, err)
      }
    })
  }

  /** Subscribe to an event exactly once. */
  once<T = unknown>(type: AppEventType, handler: EventHandler<T>): void {
    const wrapper: EventHandler = (event) => {
      handler(event as AppEvent<T>)
      this.off(type, wrapper)
    }
    this.on(type, wrapper)
  }

  /** Remove all listeners for a type, or all listeners if no type given. */
  clear(type?: AppEventType): void {
    if (type) {
      this.listeners.delete(type)
    } else {
      this.listeners.clear()
    }
  }

  /** Debugging: list all active subscriptions */
  listSubscriptions(): Record<string, number> {
    const result: Record<string, number> = {}
    this.listeners.forEach((handlers, type) => {
      result[type] = handlers.size
    })
    return result
  }
}

// Global singleton event bus
export const eventBus = new EventBus()
