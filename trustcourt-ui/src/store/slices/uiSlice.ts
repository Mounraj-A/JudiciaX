// ─── Redux UI Slice ───────────────────────────────────────────────────────────
import { createSlice, type PayloadAction } from '@reduxjs/toolkit'
import type { UIState, BreadcrumbEntry } from '@/types/ui'

const initialState: UIState = {
  theme:             'light',
  density:           'default',
  sidebarCollapsed:  false,
  sidebarMobileOpen: false,
  globalLoading:     false,
  loadingMessage:    null,
  pageTitle:         'TrustCourt',
  breadcrumbs:       [],
}

const uiSlice = createSlice({
  name: 'ui',
  initialState,
  reducers: {
    setTheme(state, action: PayloadAction<UIState['theme']>) {
      state.theme = action.payload
    },
    setDensity(state, action: PayloadAction<UIState['density']>) {
      state.density = action.payload
    },
    toggleSidebar(state) {
      state.sidebarCollapsed = !state.sidebarCollapsed
    },
    setSidebarCollapsed(state, action: PayloadAction<boolean>) {
      state.sidebarCollapsed = action.payload
    },
    setSidebarMobileOpen(state, action: PayloadAction<boolean>) {
      state.sidebarMobileOpen = action.payload
    },
    setGlobalLoading(state, action: PayloadAction<{ loading: boolean; message?: string }>) {
      state.globalLoading  = action.payload.loading
      state.loadingMessage = action.payload.message ?? null
    },
    setPageTitle(state, action: PayloadAction<string>) {
      state.pageTitle = action.payload
      document.title  = `${action.payload} — TrustCourt`
    },
    setBreadcrumbs(state, action: PayloadAction<BreadcrumbEntry[]>) {
      state.breadcrumbs = action.payload
    },
  },
})

export const {
  setTheme, setDensity,
  toggleSidebar, setSidebarCollapsed, setSidebarMobileOpen,
  setGlobalLoading, setPageTitle, setBreadcrumbs,
} = uiSlice.actions

export const uiReducer = uiSlice.reducer

// ─── UI Selectors ─────────────────────────────────────────────────────────────
import type { RootState } from '../index'

export const selectTheme            = (s: RootState) => s.ui.theme
export const selectDensity          = (s: RootState) => s.ui.density
export const selectSidebarCollapsed = (s: RootState) => s.ui.sidebarCollapsed
export const selectGlobalLoading    = (s: RootState) => s.ui.globalLoading
export const selectLoadingMessage   = (s: RootState) => s.ui.loadingMessage
export const selectPageTitle        = (s: RootState) => s.ui.pageTitle
export const selectBreadcrumbs      = (s: RootState) => s.ui.breadcrumbs
