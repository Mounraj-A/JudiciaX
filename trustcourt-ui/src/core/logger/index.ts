// ─── Frontend Logger ──────────────────────────────────────────────────────────
import { appConfig, isDev } from '@/config/env'

type LogLevel = 'debug' | 'info' | 'warn' | 'error' | 'silent'

const LEVEL_PRIORITY: Record<LogLevel, number> = {
  debug:  0,
  info:   1,
  warn:   2,
  error:  3,
  silent: 99,
}

interface LogEntry {
  level:     LogLevel
  message:   string
  data?:     unknown
  timestamp: string
  source?:   string
}

class FrontendLogger {
  private minLevel: number

  constructor() {
    this.minLevel = LEVEL_PRIORITY[appConfig.logLevel] ?? 0
  }

  private shouldLog(level: LogLevel): boolean {
    return LEVEL_PRIORITY[level] >= this.minLevel
  }

  private format(level: LogLevel, message: string, data?: unknown): LogEntry {
    return {
      level,
      message,
      data,
      timestamp: new Date().toISOString(),
    }
  }

  private output(entry: LogEntry): void {
    const prefix = `[${entry.timestamp}] [TC:${entry.level.toUpperCase()}]`

    if (isDev) {
      // Styled dev console output
      const styles: Record<LogLevel, string> = {
        debug:  'color: #6B7280',
        info:   'color: #1E3A8A; font-weight: 600',
        warn:   'color: #D97706; font-weight: 600',
        error:  'color: #B91C1C; font-weight: 700',
        silent: '',
      }
      switch (entry.level) {
        case 'debug': console.debug(`%c${prefix} ${entry.message}`, styles.debug, entry.data ?? ''); break
        case 'info':  console.info( `%c${prefix} ${entry.message}`, styles.info,  entry.data ?? ''); break
        case 'warn':  console.warn( `%c${prefix} ${entry.message}`, styles.warn,  entry.data ?? ''); break
        case 'error': console.error(`%c${prefix} ${entry.message}`, styles.error, entry.data ?? ''); break
      }
    } else {
      // Production: JSON only, errors to stderr equivalent
      if (entry.level === 'error') {
        console.error(JSON.stringify(entry))
      } else if (entry.level === 'warn') {
        console.warn(JSON.stringify(entry))
      }
      // TODO: Phase F5 – Send structured logs to monitoring service (Datadog, etc.)
    }
  }

  debug(message: string, data?: unknown): void {
    if (this.shouldLog('debug')) this.output(this.format('debug', message, data))
  }

  info(message: string, data?: unknown): void {
    if (this.shouldLog('info')) this.output(this.format('info', message, data))
  }

  warn(message: string, data?: unknown): void {
    if (this.shouldLog('warn')) this.output(this.format('warn', message, data))
  }

  error(message: string, data?: unknown): void {
    if (this.shouldLog('error')) this.output(this.format('error', message, data))
  }

  group(label: string): void {
    if (isDev) console.group(label)
  }

  groupEnd(): void {
    if (isDev) console.groupEnd()
  }
}

// Singleton
export const logger = new FrontendLogger()
