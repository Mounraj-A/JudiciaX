// â”€â”€â”€ ResetPasswordPage â€” Phase F5 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { ROUTES } from '@/constants/routes'
import { AuthLayout } from '../components/AuthLayout'
import { PasswordStrengthMeter } from '../components/PasswordStrengthMeter'
import { Input } from '@/shared/components/form'

const resetSchema = z.object({
  password: z.string()
    .min(8, 'Password must be at least 8 characters')
    .regex(/[A-Z]/, 'Password must contain at least one uppercase letter')
    .regex(/[0-9]/, 'Password must contain at least one number')
    .regex(/[^A-Za-z0-9]/, 'Password must contain at least one special character'),
  confirmPassword: z.string()
}).refine((data) => data.password === data.confirmPassword, {
  message: "Passwords don't match",
  path: ["confirmPassword"],
})

type ResetFormValues = z.infer<typeof resetSchema>

export function ResetPasswordPage() {
  const [success, setSuccess] = useState(false)
  const [error, setError] = useState('')
  const [showPass, setShowPass] = useState(false)
  const [showConfirm, setShowConfirm] = useState(false)
  const navigate = useNavigate()

  const { register, handleSubmit, control, formState: { errors, isSubmitting } } = useForm<ResetFormValues>({
    resolver: zodResolver(resetSchema),
    defaultValues: { password: '', confirmPassword: '' }
  })
  
  const watchedPassword = useWatch({ control, name: 'password', defaultValue: '' })

  const onSubmit = async (data: ResetFormValues) => {
    setError('')
    try {
      // Simulate API Call
      await new Promise(res => setTimeout(res, 1500))
      setSuccess(true)
      setTimeout(() => { navigate(ROUTES.LOGIN) }, 3000)
    } catch (err) {
      setError('Failed to reset password. The link might be expired.')
    }
  }

  if (success) {
    return (
      <AuthLayout title="Password Reset Complete" subtitle="Your password has been successfully updated.">
        <div style={{ textAlign: 'center', padding: '2rem 0', animation: 'fadeIn 0.5s ease' }}>
          <div style={{ width: 64, height: 64, background: '#D1FAE5', color: '#10B981', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '2rem', margin: '0 auto 1.5rem' }}>âœ“</div>
          <p style={{ fontSize: '0.875rem', color: '#4B5563', marginBottom: '2rem' }}>
            You can now sign in with your new credentials. Redirecting to login...
          </p>
          <Link to={ROUTES.LOGIN} style={{
            display: 'inline-block', width: '100%', padding: '0.875rem', background: '#0F1D3A', color: '#FFFFFF',
            border: 'none', borderRadius: 8, fontSize: '0.875rem', fontWeight: 600, textDecoration: 'none'
          }}>
            Go to Login Now
          </Link>
        </div>
      </AuthLayout>
    )
  }

  return (
    <AuthLayout title="Create New Password" subtitle="Choose a strong password to secure your account.">
      {error && (
        <div style={{ background: '#FEF2F2', border: '1px solid #FECACA', borderRadius: 8, padding: '12px 16px', color: '#991B1B', fontSize: '0.875rem', marginBottom: 24, display: 'flex', alignItems: 'center', gap: 8 }}>
          <span style={{ fontSize: '1.25rem' }}>âš </span> {error}
        </div>
      )}

      <form onSubmit={handleSubmit(onSubmit)} noValidate style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        <div style={{ position: 'relative' }}>
          <Input
            label="New Password"
            type={showPass ? 'text' : 'password'}
            placeholder="â€¢â€¢â€¢â€¢â€¢â€¢â€¢â€¢"
            error={errors.password?.message}
            {...register('password')}
            disabled={isSubmitting}
            rightIcon={
              <button
                type="button"
                onClick={() => setShowPass(!showPass)}
                style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#6B7280', display: 'flex' }}
              >
                {showPass ? 'ðŸ™ˆ' : 'ðŸ‘'}
              </button>
            }
          />
        </div>
        
        <PasswordStrengthMeter password={watchedPassword} />

        <div style={{ position: 'relative', marginTop: '1rem' }}>
          <Input
            label="Confirm Password"
            type={showConfirm ? 'text' : 'password'}
            placeholder="â€¢â€¢â€¢â€¢â€¢â€¢â€¢â€¢"
            error={errors.confirmPassword?.message}
            {...register('confirmPassword')}
            disabled={isSubmitting}
            rightIcon={
              <button
                type="button"
                onClick={() => setShowConfirm(!showConfirm)}
                style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#6B7280', display: 'flex' }}
              >
                {showConfirm ? 'ðŸ™ˆ' : 'ðŸ‘'}
              </button>
            }
          />
        </div>

        <button
          type="submit"
          disabled={isSubmitting}
          style={{
            width: '100%', padding: '0.875rem', background: '#0F1D3A', color: '#FFFFFF',
            border: 'none', borderRadius: 8, fontSize: '0.875rem', fontWeight: 600,
            cursor: isSubmitting ? 'not-allowed' : 'pointer', opacity: isSubmitting ? 0.8 : 1,
            marginTop: '1rem', transition: 'background-color 0.2s',
            display: 'flex', justifyContent: 'center', alignItems: 'center', gap: 8
          }}
        >
          {isSubmitting ? (
            <><span style={{ display: 'inline-block', width: 16, height: 16, border: '2px solid rgba(255,255,255,0.3)', borderTopColor: '#fff', borderRadius: '50%', animation: 'spin 1s linear infinite' }} /> Updating...</>
          ) : 'Reset Password'}
        </button>
      </form>
      <style>{`@keyframes spin { to { transform: rotate(360deg); } } @keyframes fadeIn { from { opacity: 0 } to { opacity: 1 } }`}</style>
    </AuthLayout>
  )
}
export default ResetPasswordPage
