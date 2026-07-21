// ─── Auth Feature Flags ───────────────────────────────────────────────────────
// Phase F2 – Authentication-specific feature flags
//
// Extends the base feature flag system (src/config/featureFlags.ts)
// with auth-specific gates.
//
// ⚠️  REGISTRATION FLAG:
//     Set to TRUE  in development/testing → /register route mounted, register() enabled
//     Set to FALSE in production          → /register route NOT mounted, register() throws
//     Control via: VITE_FEATURE_FLAGS={"REGISTRATION":false}
//     No code changes required.

export interface AuthFeatureFlags {
  /** Public user registration — dev/test only. See AuthService.register() for details. */
  REGISTRATION:     boolean

  /** Remember-me checkbox on login form. */
  REMEMBER_ME:      boolean

  /** Silent background token refresh before expiry. */
  SILENT_REFRESH:   boolean

  /** Restore session from storage on app reload. */
  SESSION_RECOVERY: boolean

  /** Show idle warning dialog before auto-logout. */
  IDLE_WARNING:     boolean
}

/** Default auth feature flags */
export const defaultAuthFlags: AuthFeatureFlags = {
  REGISTRATION:     import.meta.env.VITE_APP_ENV !== 'production',  // auto-off in prod
  REMEMBER_ME:      true,
  SILENT_REFRESH:   true,
  SESSION_RECOVERY: true,
  IDLE_WARNING:     true,
}

// Runtime override from VITE_AUTH_FLAGS env var
const runtimeOverrides: Partial<AuthFeatureFlags> = {}
try {
  const raw = import.meta.env.VITE_AUTH_FLAGS
  if (raw) Object.assign(runtimeOverrides, JSON.parse(raw) as Partial<AuthFeatureFlags>)
} catch { /* silent */ }

export const authFeatureFlags: Readonly<AuthFeatureFlags> = Object.freeze({
  ...defaultAuthFlags,
  ...runtimeOverrides,
})

export const isAuthFeatureEnabled = (flag: keyof AuthFeatureFlags): boolean =>
  authFeatureFlags[flag] === true
