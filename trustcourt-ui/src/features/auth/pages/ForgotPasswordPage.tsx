// â”€â”€â”€ ForgotPasswordPage â€” Phase F5 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { ROUTES } from '@/constants/routes'
import { AuthLayout } from '../components/AuthLayout'
import { Input } from '@/shared/components/form'

const forgotSchema = z.object({
  email: z.string().email('Please enter a valid email address.')
})
type ForgotFormValues = z.infer<typeof forgotSchema>

export function ForgotPasswordPage() {
  const [success, setSuccess] = useState(false)
  const [error, setError] = useState('')

  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<ForgotFormValues>({
    resolver: zodResolver(forgotSchema),
    defaultValues: { email: '' }
  })

  const onSubmit = async (data: ForgotFormValues) => {
    setError('')
    try {
      // Simulate API call
      await new Promise(res => setTimeout(res, 1500))
      // if (data.email === 'error@test.com') throw new Error('Simulated error')
      setSuccess(true)
    } catch (err) {
      setError('Failed to send reset link. Please try again.')
    }
  }

  if (success) {
    return (
      <AuthLayout title="Check your inbox" subtitle="We've sent password reset instructions to your email.">
        <div style={{ textAlign: 'center', padding: '2rem 0' }}>
          <div style={{ width: 64, height: 64, background: '#D1FAE5', color: '#10B981', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '2rem', margin: '0 auto 1.5rem' }}>âœ“</div>
          <p style={{ fontSize: '0.875rem', color: '#4B5563', marginBottom: '2rem' }}>
            If an account exists with that email, you will receive a secure link to reset your password shortly.
          </p>
          <Link to={ROUTES.LOGIN} style={{
            display: 'inline-block', width: '100%', padding: '0.875rem', background: '#F3F4F6', color: '#111827',
            border: '1px solid #E5E7EB', borderRadius: 8, fontSize: '0.875rem', fontWeight: 600, textDecoration: 'none'
          }}>
            Return to login
          </Link>
        </div>
      </AuthLayout>
    )
  }

  return (
    <AuthLayout title="Reset Password" subtitle="Enter your email to receive recovery instructions.">
      {error && (
        <div style={{ background: '#FEF2F2', border: '1px solid #FECACA', borderRadius: 8, padding: '12px 16px', color: '#991B1B', fontSize: '0.875rem', marginBottom: 24, display: 'flex', alignItems: 'center', gap: 8 }}>
          <span style={{ fontSize: '1.25rem' }}>âš </span> {error}
        </div>
      )}

      <form onSubmit={handleSubmit(onSubmit)} noValidate style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
        <Input
          label="Email Address"
          type="email"
          placeholder="e.g., judge@court.gov.in"
          error={errors.email?.message}
          {...register('email')}
          disabled={isSubmitting}
        />

        <button
          type="submit"
          disabled={isSubmitting}
          style={{
            width: '100%', padding: '0.875rem', background: '#0F1D3A', color: '#FFFFFF',
            border: 'none', borderRadius: 8, fontSize: '0.875rem', fontWeight: 600,
            cursor: isSubmitting ? 'not-allowed' : 'pointer', opacity: isSubmitting ? 0.8 : 1,
            transition: 'background-color 0.2s', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: 8
          }}
        >
          {isSubmitting ? (
            <><span style={{ display: 'inline-block', width: 16, height: 16, border: '2px solid rgba(255,255,255,0.3)', borderTopColor: '#fff', borderRadius: '50%', animation: 'spin 1s linear infinite' }} /> Sending...</>
          ) : 'Send Reset Link'}
        </button>
      </form>

      <div style={{ marginTop: '2rem', textAlign: 'center', fontSize: '0.875rem', color: '#6B7280' }}>
        Remembered your password? <Link to={ROUTES.LOGIN} style={{ color: '#1E40AF', fontWeight: 600, textDecoration: 'none' }}>Sign In</Link>
      </div>
      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </AuthLayout>
  )
}
export default ForgotPasswordPage
