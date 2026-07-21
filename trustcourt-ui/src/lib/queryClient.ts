// ─── TanStack Query Client Configuration ──────────────────────────────────────
import { QueryClient, QueryCache, MutationCache } from '@tanstack/react-query'
import { APP_CONSTANTS } from '@/constants'
import { logger } from '@/core/logger'
import { eventBus } from '@/core/events'
import { getErrorMessage } from '@/api/errors'

export function createQueryClient(): QueryClient {
  return new QueryClient({
    defaultOptions: {
      queries: {
        // Cache & staleness
        staleTime:             APP_CONSTANTS.QUERY_STALE,
        gcTime:                10 * 60 * 1000, // 10 minutes GC time
        // Retry
        retry:                 (failCount, error) => {
          // Don't retry auth/permission errors
          if (error instanceof Error && ['AuthError', 'ForbiddenError'].includes(error.name)) {
            return false
          }
          return failCount < APP_CONSTANTS.MAX_RETRIES
        },
        retryDelay:            (attempt) => Math.min(1000 * 2 ** attempt, 30_000), // Exponential backoff
        // Behavior
        refetchOnWindowFocus:  false,
        refetchOnReconnect:    true,
        networkMode:           'online',
      },
      mutations: {
        retry:       0,
        networkMode: 'online',
        onError:     (error) => {
          logger.error('[Mutation] Error', { message: getErrorMessage(error) })
        },
      },
    },
    queryCache: new QueryCache({
      onError: (error: unknown, query: any) => {
        logger.warn('[QueryCache] Error', { key: query.queryKey, message: getErrorMessage(error) })
        eventBus.emit('api:error', { error, queryKey: query.queryKey })
      },
    }),
    mutationCache: new MutationCache({
      onError: (error: unknown) => {
        logger.error('[MutationCache] Error', { message: getErrorMessage(error) })
      },
    }),
  })
}

// Singleton QueryClient
export const queryClient = createQueryClient()

// ─── Query Key Factory ────────────────────────────────────────────────────────
// Provides type-safe, structured query keys for all feature modules.
export const queryKeys = {
  // Auth
  auth:    { user: ['auth', 'user'] as const },
  // Cases
  cases: {
    all:    (params?: object)          => ['cases', params]              as const,
    byId:   (id: string)               => ['cases', id]                  as const,
    count:  ()                         => ['cases', 'count']             as const,
  },
  // Documents
  documents: {
    all:    (caseId?: string)          => ['documents', caseId]          as const,
    byId:   (id: string)               => ['documents', id]              as const,
  },
  // Hearings
  hearings: {
    all:    (params?: object)          => ['hearings', params]           as const,
    byCase: (caseId: string)           => ['hearings', 'case', caseId]  as const,
  },
  // AI (feature-level)
  ai: {
    jpiScore: (caseId: string)         => ['ai', 'jpi', caseId]         as const,
    ctsScore: (caseId: string)         => ['ai', 'cts', caseId]         as const,
    pipeline: (caseId: string)         => ['ai', 'pipeline', caseId]    as const,
    xai:      (caseId: string)         => ['ai', 'xai', caseId]         as const,
  },
  // Notifications
  notifications: {
    all:       ()                      => ['notifications']              as const,
    unread:    ()                      => ['notifications', 'unread']    as const,
    search:    (params?: object)       => ['notifications', 'search', params] as const,
    templates: ()                      => ['notifications', 'templates'] as const,
    template:  (id: string)            => ['notifications', 'templates', id] as const,
    prefs:     ()                      => ['notifications', 'prefs']    as const,
  },
  // Users (Admin)
  users: {
    all:    (params?: object)          => ['users', params]              as const,
    byId:   (id: string)               => ['users', id]                  as const,
    me:     ()                         => ['users', 'me']               as const,
    sessions: ()                       => ['users', 'me', 'sessions']   as const,
  },
  // ── ADMIN MODULE ──────────────────────────────────────────────────────────
  admin: {
    dashboard:  ()                     => ['admin', 'dashboard']         as const,
    ping:       ()                     => ['admin', 'ping']              as const,
  },
  // Courts
  courts: {
    all:        (params?: object)      => ['courts', params]             as const,
    byId:       (id: string)           => ['courts', id]                 as const,
    benches:    (courtId: string)      => ['courts', courtId, 'benches'] as const,
    rooms:      (courtId: string)      => ['courts', courtId, 'rooms']   as const,
  },
  // Roles & Permissions
  roles: {
    all:        ()                     => ['roles']                      as const,
    byId:       (id: string)           => ['roles', id]                  as const,
    permissions:(id: string)           => ['roles', id, 'permissions']   as const,
  },
  // Admin AI
  adminAi: {
    settings:   ()                     => ['admin', 'ai', 'settings']    as const,
    usage:      ()                     => ['admin', 'ai', 'usage']       as const,
    health:     ()                     => ['admin', 'ai', 'health']      as const,
    ocrQueue:   ()                     => ['admin', 'ai', 'ocr', 'queue']  as const,
    ocrMonitor: ()                     => ['admin', 'ai', 'ocr', 'monitor'] as const,
    analytics:  ()                     => ['admin', 'ai', 'analytics']   as const,
    queueStatus:()                     => ['admin', 'ai', 'queue-status'] as const,
  },
  // Workflows
  workflows: {
    all:        (params?: object)      => ['workflows', params]          as const,
    byId:       (id: string)           => ['workflows', id]              as const,
    status:     ()                     => ['workflows', 'status']        as const,
    history:    ()                     => ['workflows', 'history']       as const,
  },
  // Audit (admin)
  audit: {
    all:        (params?: object)      => ['audit', params]              as const,
    byId:       (id: string)           => ['audit', id]                  as const,
    timeline:   (correlationId: string)=> ['audit', 'timeline', correlationId] as const,
    violations: ()                     => ['audit', 'violations']        as const,
    security:   (params?: object)      => ['audit', 'security', params]  as const,
  },
  // Configurations
  config: {
    all:        ()                     => ['config']                     as const,
    byId:       (id: string)           => ['config', id]                 as const,
    byKey:      (key: string)          => ['config', 'key', key]         as const,
  },
  // Announcements
  announcements: {
    all:        ()                     => ['announcements']              as const,
    byId:       (id: string)           => ['announcements', id]          as const,
  },
  // Maintenance
  maintenance: {
    all:        ()                     => ['maintenance']                as const,
    byId:       (id: string)           => ['maintenance', id]            as const,
  },
  // Security
  security: {
    events:     (params?: object)      => ['security', 'events', params] as const,
    summary:    ()                     => ['security', 'summary']        as const,
  },
  // Reports
  reports: {
    admin:          ()                 => ['reports', 'admin']           as const,
    dashboardAdmin: ()                 => ['reports', 'dashboard', 'admin'] as const,
    dashboardCourt: ()                 => ['reports', 'dashboard', 'court'] as const,
    dashboardJudge: ()                 => ['reports', 'dashboard', 'judge'] as const,
    caseStatus:     ()                 => ['reports', 'cases', 'status']  as const,
    caseBacklog:    ()                 => ['reports', 'cases', 'backlog'] as const,
    casePriority:   ()                 => ['reports', 'cases', 'priority'] as const,
    judges:         ()                 => ['reports', 'judges']          as const,
    judgeScores:    ()                 => ['reports', 'judges', 'scores'] as const,
    courts:         ()                 => ['reports', 'courts']          as const,
    courtStatus:    ()                 => ['reports', 'courts', 'status'] as const,
    perfMonthly:    ()                 => ['reports', 'perf', 'monthly']  as const,
    perfQuarterly:  ()                 => ['reports', 'perf', 'quarterly'] as const,
    aiAnalytics:    ()                 => ['reports', 'ai']              as const,
    research:       ()                 => ['reports', 'research']        as const,
  },
  // Judges (admin)
  judges: {
    workloads:  ()                     => ['judges', 'workloads']        as const,
    workload:   (id: string)           => ['judges', id, 'workload']     as const,
  },
  // Clerks (admin)
  clerks: {
    stats:      (id: string)           => ['clerks', id, 'stats']        as const,
  },
} as const
