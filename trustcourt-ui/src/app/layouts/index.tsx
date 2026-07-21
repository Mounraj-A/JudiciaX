import { Suspense, useMemo, useState, useEffect, useRef } from 'react'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import { WorkspaceLayout as DSWorkspaceLayout } from '@/shared/design-system/layouts'
import { TopNav, Sidebar, Breadcrumb } from '@/shared/design-system/components/navigation'
import { LoadingSpinner } from '@/shared/design-system/components/feedback'

import { Bell, User, LogOut } from 'lucide-react'
import { useAppSelector, useAppDispatch } from '@/store'
import { selectSidebarCollapsed, toggleSidebar, selectBreadcrumbs } from '@/store/slices/uiSlice'
import { selectUserRole, selectCurrentUser } from '@/store/slices/authSlice'
import { getNavigationForRole } from '@/app/menu'
import { useUnreadNotificationCount } from '@/features/admin/hooks/useAdminNotifications'
import { useAuthContext } from '@/features/auth/contexts/AuthContext'

// ─── Suspense Fallback ────────────────────────────────────────────────────────
export function LayoutSuspenseFallback() {
  return (
    <div className="flex h-full min-h-[400px] w-full items-center justify-center bg-[#F8F9FA]">
      <LoadingSpinner size="md" label="Loading page module..." />
    </div>
  )
}

// ─── Marketing Layout ─────────────────────────────────────────────────────────
/**
 * Hosts the public-facing marketing website:
 *   - Landing page
 *   - Future documentation / blog / public API pages
 * The landing page manages its own nav internally, so this layout is transparent.
 */
export function MarketingLayout() {
  return (
    <div style={{ minHeight: '100vh' }}>
      <Suspense fallback={<LayoutSuspenseFallback />}>
        <Outlet />
      </Suspense>
    </div>
  )
}

/**
 * @deprecated Use MarketingLayout instead.
 * Kept as alias for backward-compatibility with any direct imports.
 */
export const PublicLayout = MarketingLayout

// ─── Protected Layout (App Shell) ─────────────────────────────────────────────
/** Main authenticated layout wrapper with dynamic role navigation and top navigation. */
export function ProtectedLayout() {
  const dispatch         = useAppDispatch()
  const navigate         = useNavigate()
  const location         = useLocation()
  const sidebarCollapsed = useAppSelector(selectSidebarCollapsed)
  const breadcrumbs      = useAppSelector(selectBreadcrumbs)
  const userRole         = useAppSelector(selectUserRole)
  const currentUser      = useAppSelector(selectCurrentUser)
  const { data: unreadCount = 0 } = useUnreadNotificationCount()
  const { logout } = useAuthContext()

  const [profileMenuOpen, setProfileMenuOpen] = useState(false)
  const menuRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setProfileMenuOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => {
      document.removeEventListener('mousedown', handleClickOutside)
    }
  }, [menuRef])

  const navItems = getNavigationForRole(userRole ?? 'VIEWER')

  // Map to SidebarItem expected by DS component
  const sidebarItems = useMemo(() => {
    return navItems.map((item: any) => ({
      id: item.id,
      label: item.label,
      icon: item.icon,
      href: item.path,
      badge: item.badge,
      children: item.children?.map((child: any) => ({
        id: child.id,
        label: child.label,
        href: child.path,
      }))
    }))
  }, [navItems])

  // Find active item ID based on current pathname
  const activeId = useMemo(() => {
    // Exact match
    const exactMatch = sidebarItems.find(item => item.href && location.pathname === item.href)
    if (exactMatch) return exactMatch.id
    // Prefix match for sub-routes
    const prefixMatch = sidebarItems.find(item => item.href && location.pathname.startsWith(item.href) && item.href !== '/admin')
    if (prefixMatch) return prefixMatch.id
    // Special case for dashboard root
    if (location.pathname === '/admin') return 'overview'
    return ''
  }, [sidebarItems, location.pathname])

  return (
    <>
      <DSWorkspaceLayout
        sidebarCollapsed={sidebarCollapsed}
        sidebar={
          <Sidebar
            items={sidebarItems}
            activeId={activeId}
            collapsed={sidebarCollapsed}
            onCollapse={() => dispatch(toggleSidebar())}
            onItemClick={(item) => {
              if (item.href) navigate(item.href)
            }}
          />
        }
        topNav={
          <TopNav
            title="TrustCourt Workspace"
            breadcrumb={
              breadcrumbs.length > 0 ? (
                <Breadcrumb items={breadcrumbs} />
              ) : undefined
            }
            actions={
              <div className="flex items-center gap-4 text-xs font-medium text-[#6B7280]">
                <button 
                  onClick={() => navigate('/admin/notifications')}
                  className="relative p-2 rounded-full hover:bg-black/5 transition-colors"
                  aria-label="Notifications"
                >
                  <Bell className="w-5 h-5 text-[#4B5563]" />
                  {unreadCount > 0 && (
                    <span className="absolute top-0.5 right-1 flex h-4 min-w-4 items-center justify-center rounded-full bg-red-500 px-1 text-[10px] text-white">
                      {unreadCount}
                    </span>
                  )}
                </button>

                <div className="relative" ref={menuRef}>
                  <button 
                    onClick={() => setProfileMenuOpen(!profileMenuOpen)}
                    className="w-8 h-8 rounded-full bg-[#0F1D3A] text-white flex items-center justify-center hover:ring-2 ring-offset-2 ring-[#0F1D3A] transition-all"
                    aria-label="Profile"
                  >
                    {currentUser?.name?.charAt(0)?.toUpperCase() || <User className="w-4 h-4" />}
                  </button>

                  {profileMenuOpen && (
                    <div className="absolute right-0 mt-2 w-64 bg-white rounded-xl shadow-lg border border-[#E5E7EB] z-[500] overflow-hidden">
                      <div className="p-4 border-b border-[#E5E7EB] bg-[#F8F9FA]">
                        <p className="font-semibold text-[#111827]">{currentUser?.name || 'Admin User'}</p>
                        <p className="text-sm text-[#6B7280] truncate">{currentUser?.email}</p>
                        <div className="mt-2 text-xs font-medium bg-[#E0E7FF] text-[#4338CA] px-2 py-1 rounded-md inline-block capitalize">
                          {userRole?.toLowerCase()}
                        </div>
                      </div>
                      
                      <div className="p-2">
                        <button
                          onClick={() => {
                            setProfileMenuOpen(false);
                            if (userRole === 'ADVOCATE') navigate('/advocate/profile')
                            else if (userRole === 'CLERK') navigate('/clerk/profile')
                            else if (userRole === 'JUDGE') navigate('/judge/profile')
                            else navigate('/admin/profile')
                          }}
                          className="w-full text-left px-3 py-2 text-sm text-[#4B5563] hover:bg-[#F3F4F6] rounded-md transition-colors flex items-center gap-2"
                        >
                          <User className="w-4 h-4" />
                          My Profile
                        </button>
                      </div>

                      <div className="p-2 border-t border-[#E5E7EB]">
                        <button
                          onClick={() => {
                            setProfileMenuOpen(false);
                            logout();
                          }}
                          className="w-full flex items-center gap-2 px-3 py-2 text-sm text-red-600 hover:bg-red-50 rounded-md transition-colors font-medium"
                        >
                          <LogOut className="w-4 h-4" />
                          Logout
                        </button>
                      </div>
                    </div>
                  )}
                </div>
              </div>
            }
          />
        }
      >
        <Suspense fallback={<LayoutSuspenseFallback />}>
          <Outlet />
        </Suspense>
      </DSWorkspaceLayout>
    </>
  )
}

// ─── Blank Layout ─────────────────────────────────────────────────────────────
/** Used for login, registration, full-screen errors, or printable views. */
export function BlankLayout() {
  return (
    <div className="min-h-screen bg-[#F8F9FA]">
      <Suspense fallback={<LayoutSuspenseFallback />}>
        <Outlet />
      </Suspense>
    </div>
  )
}

// ─── Error Layout ─────────────────────────────────────────────────────────────
/** Dedicated layout for 404, 403, and 500 error pages. */
export function ErrorLayout() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-[#F8F9FA] p-6">
      <div className="w-full max-w-md rounded-2xl border border-[#E5E7EB] bg-white p-8 shadow-soft text-center">
        <Suspense fallback={<LayoutSuspenseFallback />}>
          <Outlet />
        </Suspense>
      </div>
    </div>
  )
}
