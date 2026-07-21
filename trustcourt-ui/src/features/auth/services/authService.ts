// ─── Auth Service ─────────────────────────────────────────────────────────────
// Phase F2 – Core authentication orchestrator
//
// Implements IAuthService: login, logout, refresh, register, forgotPassword, resetPassword.
// Integrates: API layer → token storage → permission resolution → session init → Redux state.
import type { AppDispatch } from '@/store'
import type {
  LoginCredentials, LoginResult, RegisterRequest, RegisterResult,
  ForgotPasswordRequest, ResetPasswordRequest, AuthTokens, AuthUser,
} from '@/types/auth'
import type { UserRole }    from '@/types/auth'
import { loginApi, logoutApi, registerApi, forgotPasswordApi, resetPasswordApi } from '@/api/services/auth/authApi'
import { setCredentials, clearCredentials, setInitializing, setRememberMe } from '@/store/slices/authSlice'
import { clearPermissions } from '@/store/slices/permissionSlice'
import { clearProfile }     from '@/store/slices/userSlice'
import { tokenService }     from './tokenService'
import { sessionService }   from './sessionService'
import { permissionService } from './permissionService'
import { identityService }  from './identityService'
import { profileService }   from './profileService'
import { auditService }     from './auditService'
import { handleAuthError }  from '../errors/authErrorHandler'
import { AuthError, AuthErrorCode } from '../errors/AuthError'
import { isFeatureEnabled } from '@/config/featureFlags'
import { ROUTES }           from '@/constants/routes'
import { logger }           from '@/core/logger'

class AuthServiceClass {
  private _dispatch: AppDispatch | null = null

  init(dispatch: AppDispatch): void {
    this._dispatch = dispatch
    tokenService.init(dispatch)
    sessionService.init(dispatch)
    permissionService.init(dispatch)
    profileService.init(dispatch)
    logger.info('[AuthService] Initialised with dispatch')
  }

  private get dispatch(): AppDispatch {
    if (!this._dispatch) throw new Error('[AuthService] Not initialised — call init(dispatch)')
    return this._dispatch
  }

  // ─── Login ──────────────────────────────────────────────────────────────────
  async login(credentials: LoginCredentials): Promise<LoginResult> {
    try {
      logger.info('[AuthService] Login attempt', { email: credentials.email })
      const response = await loginApi(credentials)
      const data     = response.data

      // 1. Build tokens
      const tokens: AuthTokens = {
        accessToken:  data.accessToken,
        refreshToken: data.refreshToken,
        expiresIn:    data.expiresIn,
        tokenType:    'Bearer',
        loginTime:    data.loginTime,
      }

      // 2. Map backend user → frontend user
      const mappedUser = identityService.mapToCurrentUser(data.user)
      const authUser: AuthUser = {
        id:          mappedUser.id,
        name:        mappedUser.fullName,
        email:       mappedUser.email,
        role:        mappedUser.role as UserRole,
        permissions: mappedUser.permissions,
        isActive:    mappedUser.isActive,
        lastLoginAt: mappedUser.lastLoginAt,
        avatarUrl:   mappedUser.avatarUrl,
        courtId:     undefined,
        phoneNumber: mappedUser.phoneNumber,
        emailVerified: mappedUser.emailVerified,
        accountStatus: mappedUser.accountStatus,
      }

      // 3. Save tokens to storage (strategy based on rememberMe)
      const rememberMe = credentials.rememberMe ?? false
      this.dispatch(setRememberMe(rememberMe))
      tokenService.saveTokens(tokens)

      // 4. Resolve and write permissions ONCE
      permissionService.resolvePermissions(authUser.role)

      // 5. Set Redux auth state
      this.dispatch(setCredentials({ user: authUser, tokens }))

      // 6. Init session (idle timer + metadata)
      sessionService.initSession(rememberMe)

      // 7. Schedule dynamic refresh (85% of expiresIn)
      tokenService.scheduleRefresh(tokens.expiresIn)

      // 8. Audit
      auditService.onLogin(authUser.id, authUser.role)

      logger.info('[AuthService] Login successful', { role: authUser.role })
      return { user: authUser, tokens }
    } catch (error) {
      throw handleAuthError(error)
    }
  }

  // ─── Logout ─────────────────────────────────────────────────────────────────
  async logout(): Promise<void> {
    try {
      const token = tokenService.getAccessToken()
      auditService.onLogout('current')
      await logoutApi(token ? `Bearer ${token}` : undefined).catch(() => {
        // Best-effort — clear local state regardless of backend response
        logger.warn('[AuthService] Logout API call failed — clearing local state')
      })
    } finally {
      tokenService.clearTokens()
      sessionService.destroySession()
      this.dispatch(clearCredentials())
      this.dispatch(clearPermissions())
      this.dispatch(clearProfile())
      logger.info('[AuthService] Logout complete')
    }
  }

  // ─── Refresh ─────────────────────────────────────────────────────────────────
  async refresh(): Promise<string> {
    return tokenService.refresh()
  }

  // ─── Registration ───────────────────────────────────────────────────────────
  /**
   * Register a new user account.
   *
   * ⚠️  DEVELOPMENT/TESTING PHASE ONLY.
   *
   * This method is guarded by the REGISTRATION feature flag.
   * In production deployments:
   *   1. Set VITE_FEATURE_FLAGS={"REGISTRATION":false} or VITE_APP_ENV=production
   *   2. This method throws AuthErrorCode.REGISTRATION_DISABLED
   *   3. The /register route is not mounted by AppRouter
   *   4. User creation is handled by the Admin module (Phase F5+)
   *
   * No architectural changes are required to disable registration.
   */
  async register(request: RegisterRequest): Promise<RegisterResult> {
    if (!isFeatureEnabled('REGISTRATION' as keyof import('@/config/featureFlags').FeatureFlags)) {
      throw new AuthError(AuthErrorCode.REGISTRATION_DISABLED)
    }
    try {
      logger.info('[AuthService] Registration attempt', { email: request.email })
      const response = await registerApi(request)
      auditService.onRegistration(request.email)
      logger.info('[AuthService] Registration successful')
      return response.data
    } catch (error) {
      throw handleAuthError(error)
    }
  }

  // ─── Password Reset ──────────────────────────────────────────────────────────
  async forgotPassword(request: ForgotPasswordRequest): Promise<void> {
    try {
      await forgotPasswordApi(request)
    } catch (error) {
      throw handleAuthError(error)
    }
  }

  async resetPassword(request: ResetPasswordRequest): Promise<void> {
    try {
      await resetPasswordApi(request)
    } catch (error) {
      throw handleAuthError(error)
    }
  }

  // ─── Init marker ─────────────────────────────────────────────────────────────
  setInitializing(value: boolean): void {
    this.dispatch(setInitializing(value))
  }

  navigateToHome(role: UserRole): string {
    const roleRoutes: Record<UserRole, string> = {
      JUDGE:       ROUTES.JUDGE.DASHBOARD,
      ADVOCATE:    ROUTES.ADVOCATE.DASHBOARD,
      CLERK:       ROUTES.CLERK.DASHBOARD,
      ADMIN:       ROUTES.ADMIN.DASHBOARD,
      SUPER_ADMIN: ROUTES.ADMIN.DASHBOARD,
      AUDITOR:     ROUTES.ADMIN.AUDIT,
      AI_OPERATOR: ROUTES.AI.DASHBOARD,
      RESEARCHER:  ROUTES.RESEARCH.DASHBOARD,
      VIEWER:      ROUTES.DASHBOARD,
    }
    return roleRoutes[role] ?? ROUTES.DASHBOARD
  }
}

// Suppress unused label warning
void (null as unknown as typeof import('./profileService'))

export const authService = new AuthServiceClass()
