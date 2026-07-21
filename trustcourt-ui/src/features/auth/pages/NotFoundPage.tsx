// â”€â”€â”€ NotFoundPage â€” Phase F5 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import { Link } from 'react-router-dom'
import { Input } from '@/shared/components/form'

export function NotFoundPage() {
  return (
    <div style={{
      minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: '#F9FAFB', padding: '2rem', fontFamily: '"Inter", -apple-system, sans-serif'
    }}>
      <div style={{
        maxWidth: 480, width: '100%', background: '#FFFFFF', borderRadius: 16,
        boxShadow: '0 10px 25px -5px rgba(0,0,0,0.1), 0 8px 10px -6px rgba(0,0,0,0.1)',
        padding: '3rem 2rem', textAlign: 'center',
        animation: 'scaleIn 0.3s cubic-bezier(0.16, 1, 0.3, 1)'
      }}>
        <div style={{
          width: 80, height: 80, borderRadius: '50%', background: '#F3F4F6', color: '#4B5563',
          display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '2.5rem',
          margin: '0 auto 1.5rem', boxShadow: '0 0 0 8px rgba(243,244,246,0.5)',
          fontWeight: 800
        }}>
          404
        </div>
        <h1 style={{ margin: '0 0 0.75rem 0', fontSize: '1.5rem', fontWeight: 700, color: '#111827' }}>
          Page Not Found
        </h1>
        <p style={{ margin: '0 0 2rem 0', fontSize: '0.9375rem', color: '#6B7280', lineHeight: 1.5 }}>
          The page you are looking for might have been removed, had its name changed, or is temporarily unavailable.
        </p>
        
        <div style={{ marginBottom: '2rem', textAlign: 'left' }}>
          <Input placeholder="Search TrustCourt..." type="text" rightIcon={<span style={{ cursor: 'pointer' }}>âš²</span>} />
        </div>

        <Link to="/" style={{
          display: 'inline-block', width: '100%', padding: '0.875rem', background: '#0F1D3A', color: '#FFFFFF',
          border: 'none', borderRadius: 8, fontSize: '0.875rem', fontWeight: 600, textDecoration: 'none',
          transition: 'background-color 0.2s'
        }}>
          Return to Dashboard
        </Link>
      </div>
      <style>{`@keyframes scaleIn { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }`}</style>
    </div>
  )
}
export default NotFoundPage
