// ─── Application Configuration ────────────────────────────────────────────────

interface AppConfig {
  apiUrl:       string
  appVersion:   string
  appEnv:       'development' | 'staging' | 'production'
  buildId:      string
  buildDate:    string
  logLevel:     'debug' | 'info' | 'warn' | 'error' | 'silent'
  sentryDsn?:   string
  i18nDefault:  string
}

function loadConfig(): AppConfig {
  return {
    apiUrl:      import.meta.env.VITE_API_URL       ?? 'http://localhost:8000',
    appVersion:  import.meta.env.VITE_APP_VERSION   ?? '1.0.0',
    appEnv:     (import.meta.env.VITE_APP_ENV       ?? 'development') as AppConfig['appEnv'],
    buildId:     import.meta.env.VITE_BUILD_ID      ?? 'local',
    buildDate:   import.meta.env.VITE_BUILD_DATE    ?? new Date().toISOString(),
    logLevel:   (import.meta.env.VITE_LOG_LEVEL     ?? 'debug')       as AppConfig['logLevel'],
    sentryDsn:   import.meta.env.VITE_SENTRY_DSN,
    i18nDefault: import.meta.env.VITE_I18N_DEFAULT  ?? 'en',
  }
}

export const appConfig: Readonly<AppConfig> = Object.freeze(loadConfig())

export const isDev  = appConfig.appEnv === 'development'
export const isProd = appConfig.appEnv === 'production'
