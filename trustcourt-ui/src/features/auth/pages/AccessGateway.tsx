// â”€â”€â”€ AccessGateway â€” Phase F6 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import { useEffect, useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { useAuthContext } from '@/features/auth/contexts/AuthContext'
import { getHomeRouteForRole } from '@/core/permissions'
import { Timeline, TimelineItem } from '@/shared/components/timeline'
import { EntityAvatar } from '@/shared/components/badges'

type InitStep = { id: string; label: string; status: 'pending' | 'loading' | 'success' | 'error' }

const INITIAL_STEPS: InitStep[] = [
  { id: 'identity', label: 'Identity Verified', status: 'pending' },
  { id: 'roles', label: 'Roles & Permissions Loaded', status: 'pending' },
  { id: 'court', label: 'Assigned Court Loaded', status: 'pending' },
  { id: 'prefs', label: 'User Preferences Initialized', status: 'pending' },
  { id: 'ready', label: 'Workspace Ready', status: 'pending' },
]

export function AccessGateway() {
  const { user, isAuthenticated } = useAuthContext()
  const navigate = useNavigate()
  const location = useLocation()
  
  const [steps, setSteps] = useState<InitStep[]>(INITIAL_STEPS)
  const [progress, setProgress] = useState(0)
  const [allDone, setAllDone] = useState(false)

  const from = (location.state as { from?: { pathname: string } })?.from?.pathname

  useEffect(() => {
    if (!isAuthenticated || !user) {
      navigate('/login', { replace: true })
      return
    }

    let isMounted = true
    let currentStepIdx = 0

    const executeSteps = async () => {
      for (const _ of INITIAL_STEPS) {
        if (!isMounted) break

        // Mark as loading
        setSteps((prev) => prev.map((s, i) => i === currentStepIdx ? { ...s, status: 'loading' } : s))
        
        // Simulate step work (e.g., API calls, preference loading)
        await new Promise(res => setTimeout(res, 600))
        
        if (!isMounted) break

        // Mark as success
        setSteps((prev) => prev.map((s, i) => i === currentStepIdx ? { ...s, status: 'success' } : s))
        setProgress(Math.round(((currentStepIdx + 1) / INITIAL_STEPS.length) * 100))
        currentStepIdx++
      }

      if (isMounted) {
        setAllDone(true)
        // Wait a tiny bit to show 100% before redirect
        setTimeout(() => {
          if (isMounted) {
            navigate(from ?? getHomeRouteForRole(user.role), { replace: true })
          }
        }, 800)
      }
    }

    executeSteps()

    return () => { isMounted = false }
  }, [isAuthenticated, user, navigate, from])

  if (!user) return null

  return (
    <div style={{
      minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: '#F3F4F6', padding: '2rem', fontFamily: '"Inter", -apple-system, sans-serif'
    }}>
      <div style={{
        width: '100%', maxWidth: 900, display: 'flex', gap: '2rem', flexWrap: 'wrap',
        alignItems: 'stretch'
      }}>
        
        {/* Left Column: Welcome Card & Status */}
        <div style={{ flex: '1 1 320px', display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          
          {/* Welcome Card */}
          <div style={{
            background: '#FFFFFF', borderRadius: 16, padding: '2rem',
            boxShadow: '0 10px 25px -5px rgba(0,0,0,0.05), 0 8px 10px -6px rgba(0,0,0,0.05)',
            border: '1px solid #E5E7EB', display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center'
          }}>
            <div style={{ position: 'relative', marginBottom: '1rem' }}>
              <EntityAvatar name={user.name} size={80} />
              <div style={{
                position: 'absolute', bottom: 0, right: 0, width: 20, height: 20,
                background: '#10B981', border: '3px solid #FFFFFF', borderRadius: '50%'
              }} />
            </div>
            <h1 style={{ margin: '0 0 0.25rem 0', fontSize: '1.25rem', fontWeight: 700, color: '#111827' }}>
              Welcome, {user.name}
            </h1>
            <p style={{ margin: '0 0 1rem 0', fontSize: '0.875rem', color: '#6B7280' }}>
              {user.email}
            </p>
            <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', justifyContent: 'center' }}>
              <span style={{
                background: '#DBEAFE', color: '#1E40AF', padding: '0.25rem 0.75rem',
                borderRadius: 9999, fontSize: '0.75rem', fontWeight: 600, border: '1px solid #BFDBFE'
              }}>
                {user.role.replace('_', ' ')}
              </span>
              <span style={{
                background: '#F3F4F6', color: '#4B5563', padding: '0.25rem 0.75rem',
                borderRadius: 9999, fontSize: '0.75rem', fontWeight: 600, border: '1px solid #E5E7EB'
              }}>
                TrustCourt Platform
              </span>
            </div>
          </div>

          {/* System Status */}
          <div style={{
            background: '#FFFFFF', borderRadius: 16, padding: '1.5rem',
            boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)', border: '1px solid #E5E7EB'
          }}>
            <h3 style={{ margin: '0 0 1rem 0', fontSize: '0.875rem', fontWeight: 600, color: '#374151', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              System Status
            </h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {[
                { label: 'Authentication', status: 'Secure' },
                { label: 'Database', status: 'Connected' },
                { label: 'AI Services', status: 'Standby' }
              ].map((sys, i) => (
                <div key={i} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '0.8125rem' }}>
                  <span style={{ color: '#6B7280' }}>{sys.label}</span>
                  <span style={{ color: '#059669', fontWeight: 600, display: 'flex', alignItems: 'center', gap: 4 }}>
                    <span style={{ width: 6, height: 6, borderRadius: '50%', background: '#10B981' }} /> {sys.status}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Right Column: Timeline & Progress */}
        <div style={{
          flex: '2 1 400px', background: '#FFFFFF', borderRadius: 16, padding: '2rem',
          boxShadow: '0 10px 25px -5px rgba(0,0,0,0.05)', border: '1px solid #E5E7EB',
          display: 'flex', flexDirection: 'column'
        }}>
          <h2 style={{ margin: '0 0 1.5rem 0', fontSize: '1.5rem', fontWeight: 700, color: '#111827' }}>
            Initializing Workspace
          </h2>
          
          <div style={{ flex: 1, marginBottom: '2rem' }}>
            <Timeline>
              {steps.map((step, idx) => {
                let icon = 'â—‹'
                let color = '#D1D5DB'
                if (step.status === 'loading') { icon = 'â†»'; color = '#3B82F6' }
                if (step.status === 'success') { icon = 'âœ“'; color = '#10B981' }

                return (
                  <TimelineItem
                    key={step.id}
                    title={step.label}
                    timestamp={step.status === 'success' ? 'Done' : step.status === 'loading' ? 'Loading...' : 'Pending'}
                    icon={<span style={{ animation: step.status === 'loading' ? 'spin 1.5s linear infinite' : 'none', display: 'inline-block' }}>{icon}</span>}
                    iconColor={color}
                    last={idx === steps.length - 1}
                  />
                )
              })}
            </Timeline>
          </div>

          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', marginBottom: '0.5rem' }}>
              <span style={{ fontSize: '0.875rem', fontWeight: 600, color: '#374151' }}>
                {allDone ? 'Redirecting to workspace...' : 'Preparing environment...'}
              </span>
              <span style={{ fontSize: '1.125rem', fontWeight: 700, color: '#0F1D3A' }}>
                {progress}%
              </span>
            </div>
            <div style={{ width: '100%', height: 8, background: '#F3F4F6', borderRadius: 4, overflow: 'hidden' }}>
              <div style={{
                height: '100%', width: `${progress}%`, background: 'linear-gradient(90deg, #1E40AF, #7C3AED)',
                transition: 'width 0.4s cubic-bezier(0.4, 0, 0.2, 1)', borderRadius: 4
              }} />
            </div>
          </div>
        </div>
      </div>
      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </div>
  )
}

export default AccessGateway
