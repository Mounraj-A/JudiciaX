// ─── Application Route Constants — Grouped Module Architecture ────────────────
// Each workspace is self-contained. No standalone /ai or /reports modules.
// Legacy flat properties kept for backward-compat during migration.

// ── Workspace Root Paths ────────────────────────────────────────────────────
const ADVOCATE_ROOT = '/advocate' as const
const CLERK_ROOT    = '/clerk'    as const
const JUDGE_ROOT    = '/judge'    as const
const ADMIN_ROOT    = '/admin'    as const

export const ROUTES = {

  // ── PUBLIC ─────────────────────────────────────────────────────────────────
  PUBLIC: {
    HOME:     '/'      as const,
    ABOUT:    '/#about'     as const,
    FEATURES: '/#features'  as const,
    FRAMEWORK:'/#framework' as const,
    RESEARCH: '/#research'  as const,
    CONTACT:  '/#contact'   as const,
  },

  // ── AUTHENTICATION ──────────────────────────────────────────────────────────
  AUTH: {
    LOGIN:           '/login'            as const,
    FORGOT_PASSWORD: '/forgot-password'  as const,
    RESET_PASSWORD:  '/reset-password'   as const,
    REGISTER:        '/register'         as const,
    SESSION_EXPIRED: '/session-expired'  as const,
  },

  // ── GATEWAY ─────────────────────────────────────────────────────────────────
  GATEWAY: {
    ROOT: '/gateway' as const,
  },

  // ── ADVOCATE WORKSPACE ──────────────────────────────────────────────────────
  ADVOCATE: {
    ROOT:          ADVOCATE_ROOT,
    DASHBOARD:     ADVOCATE_ROOT,
    CASES:         `${ADVOCATE_ROOT}/cases`,
    CASE:          `${ADVOCATE_ROOT}/cases/:id`,
    NEW_CASE:      `${ADVOCATE_ROOT}/cases/new`,
    DRAFTS:        `${ADVOCATE_ROOT}/drafts`,
    DOCUMENTS:     `${ADVOCATE_ROOT}/documents`,
    HEARINGS:      `${ADVOCATE_ROOT}/hearings`,
    NOTIFICATIONS: `${ADVOCATE_ROOT}/notifications`,
    PROFILE:       `${ADVOCATE_ROOT}/profile`,
    REPORTS:       `${ADVOCATE_ROOT}/reports`,
  },

  // ── CLERK WORKSPACE ─────────────────────────────────────────────────────────
  CLERK: {
    ROOT:          CLERK_ROOT,
    DASHBOARD:     CLERK_ROOT,
    SUBMISSIONS:   `${CLERK_ROOT}/submissions`,
    SCRUTINY:      `${CLERK_ROOT}/scrutiny/:id`,
    DOCUMENTS:     `${CLERK_ROOT}/documents/:id`,
    REGISTRATION:  `${CLERK_ROOT}/registration/:id`,
    HEARINGS:      `${CLERK_ROOT}/hearings`,
    RETURNED:      `${CLERK_ROOT}/returned`,
    REJECTED:      `${CLERK_ROOT}/rejected`,
    NOTIFICATIONS: `${CLERK_ROOT}/notifications`,
    PROFILE:       `${CLERK_ROOT}/profile`,
    REPORTS:       `${CLERK_ROOT}/reports`,
  },

  // ── JUDGE WORKSPACE ─────────────────────────────────────────────────────────
  JUDGE: {
    ROOT:          JUDGE_ROOT,
    DASHBOARD:     JUDGE_ROOT,
    ASSIGNED:      `${JUDGE_ROOT}/assigned`,
    REVIEW:        `${JUDGE_ROOT}/review/:id`,
    EVIDENCE:      `${JUDGE_ROOT}/evidence/:id`,
    DOCUMENT:      `${JUDGE_ROOT}/document/:id`,
    CAUSE_LIST:    `${JUDGE_ROOT}/cause-list`,
    RESERVED:      `${JUDGE_ROOT}/reserved`,
    AI_INSIGHTS:   `${JUDGE_ROOT}/ai-insights`,
    NOTIFICATIONS: `${JUDGE_ROOT}/notifications`,
    PROFILE:       `${JUDGE_ROOT}/profile`,
    REPORTS:       `${JUDGE_ROOT}/reports`,
  },

  // ── ADMIN WORKSPACE ─────────────────────────────────────────────────────────
  ADMIN: {
    ROOT:          ADMIN_ROOT,
    DASHBOARD:     ADMIN_ROOT,
    USERS:         `${ADMIN_ROOT}/users`,
    ROLES:         `${ADMIN_ROOT}/roles`,
    COURTS:        `${ADMIN_ROOT}/courts`,
    JUDGES:        `${ADMIN_ROOT}/judges`,
    ADVOCATES:     `${ADMIN_ROOT}/advocates`,
    CLERKS:        `${ADMIN_ROOT}/clerks`,
    CASES:         `${ADMIN_ROOT}/cases`,
    DOCUMENTS:     `${ADMIN_ROOT}/documents`,
    AI_SERVICES:   `${ADMIN_ROOT}/ai-services`,
    WORKFLOW:      `${ADMIN_ROOT}/workflow`,
    NOTIFICATIONS: `${ADMIN_ROOT}/notifications`,
    AUDIT:         `${ADMIN_ROOT}/audit`,
    REPORTS:       `${ADMIN_ROOT}/reports`,
    SETTINGS:      `${ADMIN_ROOT}/settings`,
    HEALTH:        `${ADMIN_ROOT}/health`,
  },

  // ── DEVELOPER ROUTES (non-production) ────────────────────────────────────────
  DEVELOPER: {
    SHOWCASE:      '/showcase'      as const,
    DESIGN_SYSTEM: '/design-system' as const,
  },

  // ── ERROR PAGES ──────────────────────────────────────────────────────────────
  ERROR: {
    NOT_FOUND:    '/404'         as const,
    FORBIDDEN:    '/403'         as const,
    SERVER_ERROR: '/500'         as const,
    MAINTENANCE:  '/maintenance' as const,
  },

  // ── LEGACY FLAT ALIASES (backward-compat — consumed by guards & services) ───
  ROOT:             '/'               as const,
  LOGIN:            '/login'          as const,
  FORGOT_PW:        '/forgot-password'as const,
  FORGOT_PASSWORD:  '/forgot-password'as const,
  RESET_PW:         '/reset-password' as const,
  RESET_PASSWORD:   '/reset-password' as const,
  REGISTER:         '/register'       as const,
  SESSION_EXPIRED:  '/session-expired'as const,
  MAINTENANCE:      '/maintenance'    as const,
  NOT_FOUND:        '/404'            as const,
  FORBIDDEN:        '/403'            as const,
  SERVER_ERROR:     '/500'            as const,
  DASHBOARD:        ADVOCATE_ROOT,
  PROFILE:          `${ADVOCATE_ROOT}/profile`,
  NOTIFICATIONS:    `${ADVOCATE_ROOT}/notifications`,
  SETTINGS:         `${ADMIN_ROOT}/settings`,

  // Deprecated module aliases (redirect into workspace modules)
  AI: {
    ROOT:       `${JUDGE_ROOT}/ai-insights`,
    DASHBOARD:  `${JUDGE_ROOT}/ai-insights`,
    JPI:        `${JUDGE_ROOT}/ai-insights`,
    CTS:        `${JUDGE_ROOT}/ai-insights`,
    XAI:        `${JUDGE_ROOT}/ai-insights`,
    PIPELINE:   `${ADMIN_ROOT}/ai-services`,
    GOVERNANCE: `${ADMIN_ROOT}/ai-services`,
  },
  RESEARCH: {
    ROOT:        '/research',
    DASHBOARD:   '/research',
    EXPERIMENTS: '/research/experiments',
    REPORTS:     '/research/reports',
  },
  REPORTS: {
    ROOT:  `${ADMIN_ROOT}/reports`,
    CASES: `${ADMIN_ROOT}/reports`,
    AI:    `${ADMIN_ROOT}/reports`,
    AUDIT: `${ADMIN_ROOT}/audit`,
  },
} as const

export type RouteKey = typeof ROUTES
