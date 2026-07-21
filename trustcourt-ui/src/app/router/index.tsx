// ─── Application Router Provider Configuration ────────────────────────────────
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import { appRoutes } from './routes'

const router = createBrowserRouter(appRoutes)

export function AppRouterProvider() {
  return <RouterProvider router={router} />
}

export { router }
