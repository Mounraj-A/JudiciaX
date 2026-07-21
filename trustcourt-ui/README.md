# TrustCourt UI — Enterprise Judicial Frontend Platform

> **Phase F0 – Design System** ✅ · **Phase F1 – Enterprise Application Foundation** ✅  
> **React 19** · **TypeScript Strict** · **Vite** · **Redux Toolkit** · **TanStack Query** · **Axios**

---

## 🏛️ Architecture Overview

The TrustCourt Enterprise Frontend is architected using **SOLID Principles**, **Clean Architecture**, and **Feature-Driven Design**. The application is structured into clearly separated layers ensuring that future feature modules (`Judge Workspace`, `Advocate Portal`, `Clerk Registry`, `Explainable AI Platform`) can be built purely as isolated modules in `src/features/` without modifying core infrastructure.

```
+-----------------------------------------------------------------------------------+
|                                  BROWSER WINDOW                                   |
+-----------------------------------------------------------------------------------+
                                         │
                                         ▼
+-----------------------------------------------------------------------------------+
|                        GLOBAL ERROR BOUNDARY (AppError)                           |
+-----------------------------------------------------------------------------------+
                                         │
                                         ▼
+-----------------------------------------------------------------------------------+
|                       REDUX STORE PROVIDER (Auth, UI, Notifications)             |
+-----------------------------------------------------------------------------------+
                                         │
                                         ▼
+-----------------------------------------------------------------------------------+
|                   TANSTACK QUERY CLIENT PROVIDER (Cache, Retry, API)              |
+-----------------------------------------------------------------------------------+
                                         │
                                         ▼
+-----------------------------------------------------------------------------------+
|                      THEME PROVIDER (9 Themes + Density Modes)                    |
+-----------------------------------------------------------------------------------+
                                         │
                                         ▼
+-----------------------------------------------------------------------------------+
|                    BREAKPOINT PROVIDER (Responsive Breakpoints)                   |
+-----------------------------------------------------------------------------------+
                                         │
                                         ▼
+-----------------------------------------------------------------------------------+
|                 APP INITIALIZER (Token Restore, Idle Timer, Events)               |
+-----------------------------------------------------------------------------------+
               │                                                 │
               ▼                                                 ▼
+------------------------------+               +------------------------------------+
|  GLOBAL OVERLAYS             |               |  ROUTER ARCHITECTURE               |
|  - GlobalLoader (Framer)     |               |  - AuthGuard / RoleGuard           |
|  - ToastManager (Alerts)     |               |  - Public / Protected Layouts      |
|  - ScreenReaderAnnouncer     |               |  - Lazy Loaded Modules             |
+------------------------------+               +------------------------------------+
```

---

## 📂 Comprehensive Directory Structure

```
src/
├── app/                        # Application Root Infrastructure
│   ├── App.tsx                 # Root Router Provider wrapper
│   ├── AppLoader.tsx           # Initial bootstrap spinner
│   ├── ErrorBoundary.tsx       # Global React exception handler
│   ├── initializer/
│   │   └── AppInitializer.tsx  # Token restoration, idle timer, event bindings
│   ├── layouts/                # PublicLayout, ProtectedLayout, BlankLayout, ErrorLayout
│   ├── menu/                   # Dynamic Menu Registry with RBAC & Permission mapping
│   ├── router/
│   │   ├── guards/             # AuthGuard, GuestGuard, RoleGuard, PermissionGuard
│   │   ├── index.tsx           # AppRouterProvider
│   │   └── routes.tsx          # Full Route Tree (Public, Guest, Protected, Role, Error)
│   └── providers/
│       └── AppProviders.tsx    # Composed Provider Tree
│
├── core/                       # Core Enterprise Infrastructure
│   ├── accessibility/          # FocusManager, ScreenReaderAnnouncer, ReducedMotion
│   ├── events/                 # Global Typed EventBus (theme, auth, navigation, api)
│   ├── hooks/                  # useAuth, usePermission, useEventBus, useBreakpoint
│   ├── loading/                # GlobalLoader overlay, TopProgressBar
│   ├── logger/                 # Enterprise FrontendLogger (console styling + JSON logs)
│   ├── monitoring/             # PerformanceMonitor & RenderMonitor stubs
│   ├── notifications/          # ToastManager rendering queue
│   ├── permissions/            # ROLE_REGISTRY (7 roles) + CASL-ready checks
│   ├── responsive/             # BreakpointContext + useBreakpoint hook
│   └── session/                # StorageManager, TokenManager, IdleTimer
│
├── api/                        # HTTP & Service Layer
│   ├── client/
│   │   └── apiClient.ts        # Axios base with UUID Correlation IDs + Interceptors
│   ├── errors/                 # AppError, NetworkError, AuthError, error normalizer
│   └── services/               # API endpoint stubs (cases, documents, hearings, ai)
│
├── store/                      # Global State Management
│   ├── index.ts                # Redux store with serializable checks + logger
│   └── slices/
│       ├── authSlice.ts        # User profile, tokens, initialization status
│       ├── uiSlice.ts          # Sidebar collapse, global loader, breadcrumbs
│       └── notificationSlice.ts# Toast queue and auto-dismissal
│
├── shared/                     # Phase F0 Design System
│   └── design-system/          # Tokens, 50+ Components, ThemeProvider, Layouts
│
├── types/                      # Comprehensive Global TypeScript Definitions
│   ├── api/index.ts            # ApiResponse, PaginatedResponse, ApiError
│   ├── auth/index.ts           # UserRole, AuthUser, AuthTokens
│   ├── events/index.ts         # AppEvent, AppEventType, EventHandler
│   ├── permissions/index.ts    # PermissionString, RoleDefinition, AbilityRule
│   ├── routes/index.ts         # RouteConfig, LayoutType, GuardType, NavigationItem
│   └── ui/index.ts             # UIState, NotificationItem, BreadcrumbEntry
│
├── constants/                  # Centralized Constants
│   ├── index.ts                # PERMISSIONS, ROLE_LABELS, APP_CONSTANTS
│   └── routes.ts               # ROUTES object covering all application domains
│
├── config/                     # Application Configuration
│   ├── env.ts                  # Environment loader with defaults & dev/prod flags
│   └── featureFlags.ts         # Feature flags with runtime override capabilities
│
└── lib/                        # Third-party configuration wrappers
    └── queryClient.ts          # TanStack QueryClient + typed queryKeys factory
```

---

## 🔐 Permission & Role-Based Access Control (RBAC)

The application supports **7 distinct judicial roles**, defined in `ROLE_REGISTRY`:

1. `JUDGE` — Case review, hearing schedules, decision authoring, AI JPI/CTS review.
2. `ADVOCATE` — Case filing, document submission, hearing status checks.
3. `CLERK` — Case registration, document indexing, hearing scheduling.
4. `ADMIN` — User management, role assignment, platform settings.
5. `SUPER_ADMIN` — Unrestricted platform access (`*` route authorization).
6. `RESEARCHER` — Explainable AI metrics, evaluation benchmarks, judicial reports.
7. `VIEWER` — Read-only access to public case statuses and reports.

### Usage in Components:
```tsx
import { usePermission } from '@/core/hooks'
import { PERMISSIONS } from '@/constants'

export function ActionPanel() {
  const { can, isRole } = usePermission()

  if (!can(PERMISSIONS.DECISION_CREATE)) return null
  return <button>Author Judicial Decision</button>
}
```

### Usage in Routes:
```tsx
<RoleGuard allowedRoles={['JUDGE']}>
  <PermissionGuard requiredPermissions={PERMISSIONS.CASE_VIEW}>
    <JudgeCasesPage />
  </PermissionGuard>
</RoleGuard>
```

---

## ⚡ Session & Lifecycle Management

1. **Token Restoration**: `AppInitializer` checks `localStorage`/`sessionStorage` via `TokenManager` during startup (`isInitializing: true`).
2. **Idle Timer Tracking**: `IdleTimer` monitors `mousemove`, `keydown`, `scroll`. Warns 2 minutes prior to session expiration and emits `session:idle-timeout`.
3. **Correlation Tracing**: Every outgoing API request receives unique `X-Correlation-ID` and `X-Request-ID` headers for end-to-end audit logs.
4. **Error Normalization**: `apiClient` response interceptor normalizes network and HTTP errors into structured `AppError` instances and emits `api:error`.

---

## 🛠️ Adding Future Feature Modules (How-To)

When building **Phase F2 (Case Management)** or **Phase F3 (Judge Workspace)**:

1. Create feature folder: `src/features/cases/` or `src/features/judge/`.
2. Define API queries in `src/api/services/` using the typed `apiClient`.
3. Register query keys in `src/lib/queryClient.ts` under `queryKeys`.
4. Add the lazy-loaded page inside `src/app/router/routes.tsx` wrapped with the appropriate `RoleGuard` and `PermissionGuard`.
5. **No core infrastructure modifications required.**

---

## 🧪 Verification & Development

```bash
# Start Vite development server
npm run dev

# Run strict TypeScript & production build
npm run build
```
