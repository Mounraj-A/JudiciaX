// ─── AuthLayout — Phase F5 ────────────────────────────────────────────────────
import React from 'react'

export interface AuthLayoutProps {
  children: React.ReactNode
  title:    string
  subtitle: string
}

export function AuthLayout({ children, title, subtitle }: AuthLayoutProps) {
  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      backgroundColor: '#0A1628',
      fontFamily: '"Inter", -apple-system, sans-serif'
    }}>
      {/* LEFT PANEL: Branding & Illustration (Hidden on small screens) */}
      <div style={{
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        padding: '3rem',
        background: 'linear-gradient(135deg, #060B18 0%, #0D1B2A 50%, #0A1628 100%)',
        position: 'relative',
        overflow: 'hidden'
      }}>
        {/* Background Elements */}
        <div style={{
          position: 'absolute', top: '-10%', left: '-10%', width: '60%', height: '60%',
          background: 'radial-gradient(circle, rgba(124,58,237,0.08) 0%, transparent 70%)',
          borderRadius: '50%'
        }} />
        <div style={{
          position: 'absolute', bottom: '-10%', right: '-10%', width: '50%', height: '50%',
          background: 'radial-gradient(circle, rgba(30,64,175,0.08) 0%, transparent 70%)',
          borderRadius: '50%'
        }} />

        {/* Top Branding */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', position: 'relative', zIndex: 10 }}>
          <div style={{
            width: 48, height: 48, borderRadius: 12,
            background: 'linear-gradient(135deg, #1E40AF, #7C3AED)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: '1.5rem', boxShadow: '0 4px 20px rgba(124,58,237,0.3)'
          }}>
            ⚖
          </div>
          <div>
            <h1 style={{ margin: 0, fontSize: '1.25rem', fontWeight: 700, color: '#FFFFFF', letterSpacing: '0.025em' }}>
              TrustCourt
            </h1>
            <p style={{ margin: 0, fontSize: '0.75rem', color: '#94A3B8', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Judicial Platform
            </p>
          </div>
        </div>

        {/* Center Illustration & Tagline */}
        <div style={{ position: 'relative', zIndex: 10, maxWidth: 480, margin: 'auto 0' }}>
          <div style={{
            background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.05)',
            borderRadius: 24, padding: '2rem', backdropFilter: 'blur(12px)',
            marginBottom: '2rem', display: 'inline-flex', flexDirection: 'column', gap: '1.5rem'
          }}>
             {/* Security Badge */}
             <div style={{
               display: 'inline-flex', alignItems: 'center', gap: '0.5rem',
               background: 'rgba(16,185,129,0.1)', color: '#34D399',
               padding: '0.5rem 1rem', borderRadius: 9999, fontSize: '0.75rem', fontWeight: 600,
               border: '1px solid rgba(16,185,129,0.2)', width: 'fit-content'
             }}>
               <span>✓</span> Government-Grade Security Active
             </div>

             <h2 style={{ margin: 0, fontSize: '2.5rem', fontWeight: 700, color: '#FFFFFF', lineHeight: 1.2, letterSpacing: '-0.02em' }}>
               Secure Identity & <br />
               <span style={{ color: '#818CF8' }}>Access Management</span>
             </h2>

             <p style={{ margin: 0, fontSize: '1.125rem', color: '#94A3B8', lineHeight: 1.6 }}>
               Enterprise authentication gateway for the TrustCourt ecosystem. Delivering seamless, zero-trust access for advocates, judges, and clerks.
             </p>
          </div>
        </div>

        {/* Bottom Footer info */}
        <div style={{ position: 'relative', zIndex: 10, display: 'flex', justifyContent: 'space-between', color: '#64748B', fontSize: '0.875rem' }}>
          <span>© {new Date().getFullYear()} TrustCourt Enterprise</span>
          <span>v2.4.0-stable</span>
        </div>
      </div>

      {/* RIGHT PANEL: Form Container */}
      <div style={{
        flex: 1,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '2rem',
        backgroundColor: '#FFFFFF',
        boxShadow: '-20px 0 40px rgba(0,0,0,0.25)',
        position: 'relative',
        zIndex: 20
      }}>
        <div style={{ width: '100%', maxWidth: 440, animation: 'fadeInUp 0.4s ease-out' }}>
          <div style={{ marginBottom: '2.5rem' }}>
            <h1 style={{ margin: '0 0 0.5rem 0', fontSize: '2rem', fontWeight: 700, color: '#0F1D3A', letterSpacing: '-0.02em' }}>
              {title}
            </h1>
            <p style={{ margin: 0, fontSize: '1rem', color: '#6B7280' }}>
              {subtitle}
            </p>
          </div>
          {children}
        </div>
      </div>
      <style>{`
        @keyframes fadeInUp { from { opacity: 0; transform: translateY(20px) } to { opacity: 1; transform: translateY(0) } }
        @media (max-width: 900px) {
          div[style*="background: linear-gradient(135deg"] { display: none !important; }
        }
      `}</style>
    </div>
  )
}
