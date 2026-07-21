// ─── Application Monitoring Stubs ─────────────────────────────────────────────
import { logger } from '@/core/logger'
import { isProd } from '@/config/env'

/**
 * Performance Monitor Stub.
 * Ready for Datadog / New Relic / Sentry RUM integration in production.
 */
export class PerformanceMonitor {
  private static metrics = new Map<string, number>()

  static startMeasure(key: string): void {
    if (isProd) return
    this.metrics.set(key, performance.now())
  }

  static endMeasure(key: string): number | null {
    if (isProd) return null
    const start = this.metrics.get(key)
    if (!start) return null
    const duration = performance.now() - start
    this.metrics.delete(key)
    logger.debug(`[Perf] ${key}: ${duration.toFixed(2)}ms`)
    return duration
  }

  static recordMetric(name: string, value: number, tags?: Record<string, string>): void {
    logger.debug(`[Metric] ${name} = ${value}`, tags)
  }
}

/**
 * Memory & Render Monitor Stubs.
 */
export class RenderMonitor {
  static logRender(componentName: string, renderTimeMs: number): void {
    if (renderTimeMs > 16) {
      logger.warn(`[RenderMonitor] Slow render: ${componentName} (${renderTimeMs.toFixed(2)}ms)`)
    }
  }
}
