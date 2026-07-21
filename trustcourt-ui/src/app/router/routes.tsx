// ─── Route Tree Configuration — Refactored Module Architecture ────────────────
//
// Hierarchy:
//   Application
//   ├── Marketing  (public, no auth needed)
//   ├── Auth       (login/register — guests only, authenticated → workspace)
//   ├── Gateway    (protected — post-login initializer)
//   ├── Advocate   (protected — ADVOCATE role)
//   ├── Clerk      (protected — CLERK role)
//   ├── Judge      (protected — JUDGE role)
//   ├── Admin      (protected — ADMIN / SUPER_ADMIN roles)
//   ├── Developer  (dev-only — no auth requirement, not linked from app)
//   └── Error      (404, 403, 500, maintenance, session-expired)
//
import { lazy, Suspense } from 'react'
import type { RouteObject } from 'react-router-dom'
import { Navigate }        from 'react-router-dom'
import { ROUTES }          from '@/constants/routes'
import { PERMISSIONS }     from '@/constants'
import { MarketingLayout, ProtectedLayout, BlankLayout, ErrorLayout } from '@/app/layouts'
import { AuthGuard, RoleGuard, PermissionGuard } from '@/app/router/guards'
import { PublicRoute }     from '@/core/guards/PublicRoute'
import { authFeatureFlags } from '@/core/permissions/featureFlags'
import { AuthLoadingState } from '@/features/auth/loading/AuthLoadingState'

// ─── Suspense fallback ────────────────────────────────────────────────────────
const PageFallback = () => <AuthLoadingState message="Loading page..." />

// ─── MARKETING: Landing ───────────────────────────────────────────────────────
const LandingPage = lazy(() =>
  import('@/features/public/landing/LandingPage').then((m) => ({ default: m.LandingPage }))
)

// ─── DEVELOPER: Design System Showcase ───────────────────────────────────────
const ShowcasePage = lazy(() => import('@/showcase/ShowcasePage'))

// ─── AUTH: Authentication pages ──────────────────────────────────────────────
const LoginPage          = lazy(() => import('@/features/auth/pages/LoginPage').then((m) => ({ default: m.LoginPage })))
const ForgotPasswordPage = lazy(() => import('@/features/auth/pages/ForgotPasswordPage').then((m) => ({ default: m.ForgotPasswordPage })))
const ResetPasswordPage  = lazy(() => import('@/features/auth/pages/ResetPasswordPage').then((m) => ({ default: m.ResetPasswordPage })))
const RegisterPage       = lazy(() => import('@/features/auth/pages/RegisterPage').then((m) => ({ default: m.RegisterPage })))
const SessionExpiredPage = lazy(() => import('@/features/auth/pages/SessionExpiredPage').then((m) => ({ default: m.SessionExpiredPage })))

// ─── GATEWAY: Access Gateway & Application Initializer ───────────────────────
const AccessGateway = lazy(() => import('@/features/auth/pages/AccessGateway').then((m) => ({ default: m.AccessGateway })))

// ─── ERROR PAGES ──────────────────────────────────────────────────────────────
const UnauthorizedPage = lazy(() => import('@/features/auth/pages/UnauthorizedPage').then((m) => ({ default: m.UnauthorizedPage })))
const NotFoundPage     = lazy(() => import('@/features/auth/pages/NotFoundPage').then((m) => ({ default: m.NotFoundPage })))
const MaintenancePage  = lazy(() => import('@/features/auth/pages/MaintenancePage').then((m) => ({ default: m.MaintenancePage })))
const ServerErrorPage  = lazy(() => import('@/features/auth/pages/NotFoundPage').then((m) => ({ default: m.NotFoundPage })))

// ─── ADVOCATE MODULE (Phase F7) ───────────────────────────────────────────────
const AdvocateDashboardPage     = lazy(() => import('@/features/advocate/pages').then(m => ({ default: m.AdvocateDashboardPage })))
const MyCasesPage               = lazy(() => import('@/features/advocate/pages').then(m => ({ default: m.MyCasesPage })))
const DraftCasesPage            = lazy(() => import('@/features/advocate/pages').then(m => ({ default: m.DraftCasesPage })))
const CaseDetailsPage           = lazy(() => import('@/features/advocate/pages').then(m => ({ default: m.CaseDetailsPage })))
const NewCaseWizardPage         = lazy(() => import('@/features/advocate/pages').then(m => ({ default: m.NewCaseWizardPage })))
const DocumentCenterPage        = lazy(() => import('@/features/advocate/pages').then(m => ({ default: m.DocumentCenterPage })))
const HearingTimelinePage       = lazy(() => import('@/features/advocate/pages').then(m => ({ default: m.HearingTimelinePage })))
const AdvocateNotificationsPage = lazy(() => import('@/features/advocate/pages').then(m => ({ default: m.AdvocateNotificationsPage })))
const AdvocateProfilePage       = lazy(() => import('@/features/advocate/pages').then(m => ({ default: m.AdvocateProfilePage })))

// ─── CLERK MODULE (Phase F8) ──────────────────────────────────────────────────
const ClerkDashboardPage        = lazy(() => import('@/features/clerk/pages').then(m => ({ default: m.ClerkDashboardPage })))
const NewSubmissionsPage        = lazy(() => import('@/features/clerk/pages').then(m => ({ default: m.NewSubmissionsPage })))
const ScrutinyWorkspacePage     = lazy(() => import('@/features/clerk/pages').then(m => ({ default: m.ScrutinyWorkspacePage })))
const DocumentVerificationPage  = lazy(() => import('@/features/clerk/pages').then(m => ({ default: m.DocumentVerificationPage })))
const CaseRegistrationPage      = lazy(() => import('@/features/clerk/pages').then(m => ({ default: m.CaseRegistrationPage })))
const HearingSchedulingPage     = lazy(() => import('@/features/clerk/pages').then(m => ({ default: m.HearingSchedulingPage })))
const ReturnedCasesPage         = lazy(() => import('@/features/clerk/pages').then(m => ({ default: m.ReturnedCasesPage })))
const RejectedCasesPage         = lazy(() => import('@/features/clerk/pages').then(m => ({ default: m.RejectedCasesPage })))
const ClerkNotificationsPage    = lazy(() => import('@/features/clerk/pages').then(m => ({ default: m.ClerkNotificationsPage })))
const ClerkProfilePage          = lazy(() => import('@/features/clerk/pages').then(m => ({ default: m.ClerkProfilePage })))

// ─── JUDGE MODULE (Phase F9) ──────────────────────────────────────────────────
const JudgeDashboardPage      = lazy(() => import('@/features/judge/pages').then(m => ({ default: m.JudgeDashboardPage })))
const AssignedCasesPage       = lazy(() => import('@/features/judge/pages').then(m => ({ default: m.AssignedCasesPage })))
const CaseReviewWorkspacePage = lazy(() => import('@/features/judge/pages').then(m => ({ default: m.CaseReviewWorkspacePage })))
const EvidenceWorkspacePage   = lazy(() => import('@/features/judge/pages').then(m => ({ default: m.EvidenceWorkspacePage })))
const DocumentViewerPage      = lazy(() => import('@/features/judge/pages').then(m => ({ default: m.DocumentViewerPage })))
const CauseListPage           = lazy(() => import('@/features/judge/pages').then(m => ({ default: m.CauseListPage })))
const ReservedJudgmentsPage   = lazy(() => import('@/features/judge/pages').then(m => ({ default: m.ReservedJudgmentsPage })))
const JudgeNotificationsPage  = lazy(() => import('@/features/judge/pages').then(m => ({ default: m.JudgeNotificationsPage })))
const JudgeProfilePage        = lazy(() => import('@/features/judge/pages').then(m => ({ default: m.JudgeProfilePage })))

// ─── ADMIN MODULE (Phase F10) ─────────────────────────────────────────────────
const AdminDashboardPage     = lazy(() => import('@/features/admin/pages').then(m => ({ default: m.AdminDashboardPage })))
const UserManagementPage     = lazy(() => import('@/features/admin/pages').then(m => ({ default: m.UserManagementPage })))
const RoleManagementPage     = lazy(() => import('@/features/admin/pages').then(m => ({ default: m.RoleManagementPage })))
const CourtManagementPage    = lazy(() => import('@/features/admin/pages').then(m => ({ default: m.CourtManagementPage })))
const JudgeManagementPage    = lazy(() => import('@/features/admin/pages').then(m => ({ default: m.JudgeManagementPage })))
const AdvocateManagementPage = lazy(() => import('@/features/admin/pages').then(m => ({ default: m.AdvocateManagementPage })))
const ClerkManagementPage    = lazy(() => import('@/features/admin/pages').then(m => ({ default: m.ClerkManagementPage })))
const CaseAdminPage          = lazy(() => import('@/features/admin/pages').then(m => ({ default: m.CaseAdminPage })))
const DocumentAdminPage      = lazy(() => import('@/features/admin/pages').then(m => ({ default: m.DocumentAdminPage })))
const AIServicesPage         = lazy(() => import('@/features/admin/pages').then(m => ({ default: m.AIServicesPage })))
const WorkflowMonitoringPage = lazy(() => import('@/features/admin/pages').then(m => ({ default: m.WorkflowMonitoringPage })))
const AdminNotificationsPage = lazy(() => import('@/features/admin/pages').then(m => ({ default: m.AdminNotificationsPage })))
const AuditLogsPage          = lazy(() => import('@/features/admin/pages').then(m => ({ default: m.AuditLogsPage })))
const AdminReportsPage       = lazy(() => import('@/features/admin/pages').then(m => ({ default: m.AdminReportsPage })))
const AdminSettingsPage      = lazy(() => import('@/features/admin/pages').then(m => ({ default: m.AdminSettingsPage })))
const SystemHealthPage       = lazy(() => import('@/features/admin/pages').then(m => ({ default: m.SystemHealthPage })))

// ─── Application Route Tree ───────────────────────────────────────────────────
export const appRoutes: RouteObject[] = [

  // ══════════════════════════════════════════════════════════════════════════════
  // 1. MARKETING — Public landing website (no auth required)
  //    Landing page manages its own nav/footer internally.
  // ══════════════════════════════════════════════════════════════════════════════
  {
    element: <MarketingLayout />,
    children: [
      {
        path: ROUTES.ROOT,
        element: (
          <PublicRoute>
            <Suspense fallback={<PageFallback />}>
              <LandingPage />
            </Suspense>
          </PublicRoute>
        ),
      },
    ],
  },

  // ══════════════════════════════════════════════════════════════════════════════
  // 2. AUTHENTICATION — Login, forgot password, reset password
  //    Wrapped in PublicRoute: authenticated users → workspace home
  // ══════════════════════════════════════════════════════════════════════════════
  {
    element: <BlankLayout />,
    children: [
      {
        path: ROUTES.AUTH.LOGIN,
        element: (
          <PublicRoute>
            <Suspense fallback={<PageFallback />}>
              <LoginPage />
            </Suspense>
          </PublicRoute>
        ),
      },
      {
        path: ROUTES.AUTH.FORGOT_PASSWORD,
        element: (
          <PublicRoute>
            <Suspense fallback={<PageFallback />}>
              <ForgotPasswordPage />
            </Suspense>
          </PublicRoute>
        ),
      },
      {
        path: ROUTES.AUTH.RESET_PASSWORD,
        element: (
          <PublicRoute>
            <Suspense fallback={<PageFallback />}>
              <ResetPasswordPage />
            </Suspense>
          </PublicRoute>
        ),
      },
      // Registration — guarded by feature flag (dev/test only)
      ...(authFeatureFlags.REGISTRATION
        ? [{
            path: ROUTES.AUTH.REGISTER,
            element: (
              <Suspense fallback={<PageFallback />}>
                <RegisterPage />
              </Suspense>
            ),
          }]
        : []),
      {
        path: ROUTES.AUTH.SESSION_EXPIRED,
        element: (
          <Suspense fallback={<PageFallback />}>
            <SessionExpiredPage />
          </Suspense>
        ),
      },
    ],
  },

  // ══════════════════════════════════════════════════════════════════════════════
  // 3. GATEWAY — Access Gateway & Application Initializer
  //    Auth required. Runs full init sequence then redirects to role workspace.
  // ══════════════════════════════════════════════════════════════════════════════
  {
    element: <BlankLayout />,
    children: [
      {
        path: ROUTES.GATEWAY.ROOT,
        element: (
          <AuthGuard>
            <Suspense fallback={<PageFallback />}>
              <AccessGateway />
            </Suspense>
          </AuthGuard>
        ),
      },
    ],
  },

  // ══════════════════════════════════════════════════════════════════════════════
  // 4. PROTECTED WORKSPACES — Role-based, all wrapped in AuthGuard + ProtectedLayout
  // ══════════════════════════════════════════════════════════════════════════════
  {
    element: (
      <AuthGuard>
        <ProtectedLayout />
      </AuthGuard>
    ),
    children: [

      // ── 4a. ADVOCATE WORKSPACE ────────────────────────────────────────────
      {
        path: ROUTES.ADVOCATE.ROOT,
        element: <RoleGuard allowedRoles={['ADVOCATE']}><AdvocateDashboardPage /></RoleGuard>,
      },
      {
        path: ROUTES.ADVOCATE.CASES,
        element: (
          <RoleGuard allowedRoles={['ADVOCATE']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
              <MyCasesPage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.ADVOCATE.DRAFTS,
        element: (
          <RoleGuard allowedRoles={['ADVOCATE']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
              <DraftCasesPage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.ADVOCATE.CASE,
        element: (
          <RoleGuard allowedRoles={['ADVOCATE']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
              <CaseDetailsPage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.ADVOCATE.NEW_CASE,
        element: (
          <RoleGuard allowedRoles={['ADVOCATE']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_CREATE}>
              <NewCaseWizardPage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.ADVOCATE.DOCUMENTS,
        element: (
          <RoleGuard allowedRoles={['ADVOCATE']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
              <DocumentCenterPage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.ADVOCATE.HEARINGS,
        element: (
          <RoleGuard allowedRoles={['ADVOCATE']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
              <HearingTimelinePage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.ADVOCATE.NOTIFICATIONS,
        element: <RoleGuard allowedRoles={['ADVOCATE']}><AdvocateNotificationsPage /></RoleGuard>,
      },
      {
        path: ROUTES.ADVOCATE.PROFILE,
        element: <RoleGuard allowedRoles={['ADVOCATE']}><AdvocateProfilePage /></RoleGuard>,
      },

      // ── 4b. CLERK WORKSPACE ───────────────────────────────────────────────
      {
        path: ROUTES.CLERK.ROOT,
        element: <RoleGuard allowedRoles={['CLERK']}><ClerkDashboardPage /></RoleGuard>,
      },
      {
        path: ROUTES.CLERK.SUBMISSIONS,
        element: (
          <RoleGuard allowedRoles={['CLERK']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
              <NewSubmissionsPage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.CLERK.SCRUTINY,
        element: (
          <RoleGuard allowedRoles={['CLERK']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
              <ScrutinyWorkspacePage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.CLERK.DOCUMENTS,
        element: (
          <RoleGuard allowedRoles={['CLERK']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
              <DocumentVerificationPage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.CLERK.REGISTRATION,
        element: (
          <RoleGuard allowedRoles={['CLERK']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
              <CaseRegistrationPage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.CLERK.HEARINGS,
        element: (
          <RoleGuard allowedRoles={['CLERK']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
              <HearingSchedulingPage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.CLERK.RETURNED,
        element: (
          <RoleGuard allowedRoles={['CLERK']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
              <ReturnedCasesPage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.CLERK.REJECTED,
        element: (
          <RoleGuard allowedRoles={['CLERK']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
              <RejectedCasesPage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.CLERK.NOTIFICATIONS,
        element: <RoleGuard allowedRoles={['CLERK']}><ClerkNotificationsPage /></RoleGuard>,
      },
      {
        path: ROUTES.CLERK.PROFILE,
        element: <RoleGuard allowedRoles={['CLERK']}><ClerkProfilePage /></RoleGuard>,
      },

      // ── 4c. JUDGE WORKSPACE ───────────────────────────────────────────────
      {
        path: ROUTES.JUDGE.ROOT,
        element: <RoleGuard allowedRoles={['JUDGE']}><JudgeDashboardPage /></RoleGuard>,
      },
      {
        path: ROUTES.JUDGE.ASSIGNED,
        element: (
          <RoleGuard allowedRoles={['JUDGE']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
              <AssignedCasesPage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.JUDGE.REVIEW,
        element: (
          <RoleGuard allowedRoles={['JUDGE']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
              <CaseReviewWorkspacePage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.JUDGE.EVIDENCE,
        element: (
          <RoleGuard allowedRoles={['JUDGE']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
              <EvidenceWorkspacePage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.JUDGE.DOCUMENT,
        element: (
          <RoleGuard allowedRoles={['JUDGE']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
              <DocumentViewerPage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.JUDGE.CAUSE_LIST,
        element: (
          <RoleGuard allowedRoles={['JUDGE']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
              <CauseListPage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.JUDGE.RESERVED,
        element: (
          <RoleGuard allowedRoles={['JUDGE']}>
            <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
              <ReservedJudgmentsPage />
            </PermissionGuard>
          </RoleGuard>
        ),
      },
      {
        path: ROUTES.JUDGE.NOTIFICATIONS,
        element: <RoleGuard allowedRoles={['JUDGE']}><JudgeNotificationsPage /></RoleGuard>,
      },
      {
        path: ROUTES.JUDGE.PROFILE,
        element: <RoleGuard allowedRoles={['JUDGE']}><JudgeProfilePage /></RoleGuard>,
      },

      // ── 4d. ADMIN WORKSPACE ───────────────────────────────────────────────
      {
        path: ROUTES.ADMIN.ROOT,
        element: <RoleGuard allowedRoles={['ADMIN', 'SUPER_ADMIN']}><AdminDashboardPage /></RoleGuard>,
      },
      {
        path: ROUTES.ADMIN.USERS,
        element: <RoleGuard allowedRoles={['ADMIN', 'SUPER_ADMIN']}><UserManagementPage /></RoleGuard>,
      },
      {
        path: ROUTES.ADMIN.ROLES,
        element: <RoleGuard allowedRoles={['ADMIN', 'SUPER_ADMIN']}><RoleManagementPage /></RoleGuard>,
      },
      {
        path: ROUTES.ADMIN.COURTS,
        element: <RoleGuard allowedRoles={['ADMIN', 'SUPER_ADMIN']}><CourtManagementPage /></RoleGuard>,
      },
      {
        path: ROUTES.ADMIN.JUDGES,
        element: <RoleGuard allowedRoles={['ADMIN', 'SUPER_ADMIN']}><JudgeManagementPage /></RoleGuard>,
      },
      {
        path: ROUTES.ADMIN.ADVOCATES,
        element: <RoleGuard allowedRoles={['ADMIN', 'SUPER_ADMIN']}><AdvocateManagementPage /></RoleGuard>,
      },
      {
        path: ROUTES.ADMIN.CLERKS,
        element: <RoleGuard allowedRoles={['ADMIN', 'SUPER_ADMIN']}><ClerkManagementPage /></RoleGuard>,
      },
      {
        path: ROUTES.ADMIN.CASES,
        element: <RoleGuard allowedRoles={['ADMIN', 'SUPER_ADMIN']}><CaseAdminPage /></RoleGuard>,
      },
      {
        path: ROUTES.ADMIN.DOCUMENTS,
        element: <RoleGuard allowedRoles={['ADMIN', 'SUPER_ADMIN']}><DocumentAdminPage /></RoleGuard>,
      },
      {
        path: ROUTES.ADMIN.AI_SERVICES,
        element: <RoleGuard allowedRoles={['ADMIN', 'SUPER_ADMIN']}><AIServicesPage /></RoleGuard>,
      },
      {
        path: ROUTES.ADMIN.WORKFLOW,
        element: <RoleGuard allowedRoles={['ADMIN', 'SUPER_ADMIN']}><WorkflowMonitoringPage /></RoleGuard>,
      },
      {
        path: ROUTES.ADMIN.NOTIFICATIONS,
        element: <RoleGuard allowedRoles={['ADMIN', 'SUPER_ADMIN']}><AdminNotificationsPage /></RoleGuard>,
      },
      {
        path: ROUTES.ADMIN.AUDIT,
        element: <RoleGuard allowedRoles={['ADMIN', 'SUPER_ADMIN']}><AuditLogsPage /></RoleGuard>,
      },
      {
        path: ROUTES.ADMIN.REPORTS,
        element: <RoleGuard allowedRoles={['ADMIN', 'SUPER_ADMIN']}><AdminReportsPage /></RoleGuard>,
      },
      {
        path: ROUTES.ADMIN.SETTINGS,
        element: <RoleGuard allowedRoles={['ADMIN', 'SUPER_ADMIN']}><AdminSettingsPage /></RoleGuard>,
      },
      {
        path: ROUTES.ADMIN.HEALTH,
        element: <RoleGuard allowedRoles={['ADMIN', 'SUPER_ADMIN']}><SystemHealthPage /></RoleGuard>,
      },

      // ── Legacy redirect: /dashboard → landing (unauthenticated hits handled by AuthGuard) ──
      {
        path: '/dashboard',
        element: <Navigate to={ROUTES.ROOT} replace />,
      },
    ],
  },

  // ══════════════════════════════════════════════════════════════════════════════
  // 5. DEVELOPER ROUTES — Design system showcase (dev only, no auth needed)
  //    Accessible at /showcase and /design-system only.
  //    NOT the default route. NOT linked from the application.
  // ══════════════════════════════════════════════════════════════════════════════
  {
    element: <BlankLayout />,
    children: [
      {
        path: ROUTES.DEVELOPER.SHOWCASE,
        element: (
          <Suspense fallback={<PageFallback />}>
            <ShowcasePage />
          </Suspense>
        ),
      },
      {
        path: ROUTES.DEVELOPER.DESIGN_SYSTEM,
        element: (
          <Suspense fallback={<PageFallback />}>
            <ShowcasePage />
          </Suspense>
        ),
      },
    ],
  },

  // ══════════════════════════════════════════════════════════════════════════════
  // 6. ERROR PAGES
  // ══════════════════════════════════════════════════════════════════════════════
  {
    element: <ErrorLayout />,
    children: [
      {
        path: ROUTES.ERROR.FORBIDDEN,
        element: <Suspense fallback={<PageFallback />}><UnauthorizedPage /></Suspense>,
      },
      {
        path: ROUTES.ERROR.SERVER_ERROR,
        element: <Suspense fallback={<PageFallback />}><ServerErrorPage /></Suspense>,
      },
      {
        path: ROUTES.ERROR.MAINTENANCE,
        element: <Suspense fallback={<PageFallback />}><MaintenancePage /></Suspense>,
      },
      // 404 catch-all — must be last
      {
        path: '*',
        element: <Suspense fallback={<PageFallback />}><NotFoundPage /></Suspense>,
      },
    ],
  },
]
