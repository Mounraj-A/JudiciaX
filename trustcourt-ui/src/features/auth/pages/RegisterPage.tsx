// Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬ RegisterPage Ã¢â‚¬â€ Phase F5 Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { ROUTES } from '@/constants/routes'
import { FeatureGuard } from '@/core/guards/FeatureGuard'
import { AuthLayout } from '../components/AuthLayout'
import { PasswordStrengthMeter } from '../components/PasswordStrengthMeter'
import { Input, Select, Checkbox } from '@/shared/components/form'
import { EmptyLayout } from '@/shared/components/layout'

const registerSchema = z.object({
  fullName: z.string().min(2, 'Full name is required'),
  email: z.string().email('Valid email is required'),
  username: z.string().min(4, 'Username must be at least 4 characters'),
  court: z.string().min(1, 'Please select a court'),
  role: z.string().min(1, 'Please select a role'),
  password: z.string()
    .min(8, 'Password must be at least 8 characters')
    .regex(/[A-Z]/, 'Requires uppercase')
    .regex(/[0-9]/, 'Requires number')
    .regex(/[^A-Za-z0-9]/, 'Requires special character'),
  confirmPassword: z.string(),
  acceptTerms: z.boolean().refine((val) => val === true, {
    message: 'You must accept the terms'
  })
}).refine((data) => data.password === data.confirmPassword, {
  message: "Passwords don't match",
  path: ["confirmPassword"],
})

type RegisterFormValues = z.infer<typeof registerSchema>

export function RegisterPage() {
  const [success, setSuccess] = useState(false)
  const [error, setError] = useState('')
  const [showPass, setShowPass] = useState(false)

  const { register, handleSubmit, control, formState: { errors, isSubmitting } } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: { fullName: '', email: '', username: '', court: '', role: '', password: '', confirmPassword: '', acceptTerms: undefined }
  })
  
  const watchedPassword = useWatch({ control, name: 'password', defaultValue: '' })

  const onSubmit = async (_data: RegisterFormValues) => {
    setError('')
    try {
      await new Promise(res => setTimeout(res, 2000))
      setSuccess(true)
    } catch (err) {
      setError('Failed to create account. Please try again.')
    }
  }

  // (In real implementation, rely on routing or FeatureGuard to block completely, but we will render it inside FeatureGuard)

  if (success) {
    return (
      <AuthLayout title="Account Created" subtitle="Your development account has been provisioned.">
        <div style={{ textAlign: 'center', padding: '2rem 0' }}>
          <div style={{ width: 64, height: 64, background: '#D1FAE5', color: '#10B981', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '2rem', margin: '0 auto 1.5rem' }}>Ã¢Å“â€œ</div>
          <p style={{ fontSize: '0.875rem', color: '#4B5563', marginBottom: '2rem' }}>
            Account successfully created. Please sign in to continue to your workspace.
          </p>
          <Link to={ROUTES.LOGIN} style={{
            display: 'inline-block', width: '100%', padding: '0.875rem', background: '#0F1D3A', color: '#FFFFFF',
            border: 'none', borderRadius: 8, fontSize: '0.875rem', fontWeight: 600, textDecoration: 'none'
          }}>
            Sign In Now
          </Link>
        </div>
      </AuthLayout>
    )
  }

  return (
    <FeatureGuard feature="REGISTRATION" fallback={<EmptyLayout title="Registration Disabled" description="Public registration is disabled in this environment. Please contact the administrator." />}>
      <AuthLayout title="Create Account" subtitle="Development environment registration only.">
        {error && (
          <div style={{ background: '#FEF2F2', border: '1px solid #FECACA', borderRadius: 8, padding: '12px 16px', color: '#991B1B', fontSize: '0.875rem', marginBottom: 24, display: 'flex', alignItems: 'center', gap: 8 }}>
            <span style={{ fontSize: '1.25rem' }}>Ã¢Å¡Â </span> {error}
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} noValidate style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <Input label="Full Name" placeholder="Jane Doe" error={errors.fullName?.message} {...register('fullName')} disabled={isSubmitting} />
            <Input label="Username" placeholder="janedoe" error={errors.username?.message} {...register('username')} disabled={isSubmitting} />
          </div>

          <Input label="Email Address" type="email" placeholder="jane@court.gov.in" error={errors.email?.message} {...register('email')} disabled={isSubmitting} />

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <Select 
              label="Court" 
              options={[{ label: 'Supreme Court', value: 'SC' }, { label: 'High Court', value: 'HC' }, { label: 'District Court', value: 'DC' }]} 
              placeholder="Select Court"
              error={errors.court?.message} 
              {...register('court')} 
              disabled={isSubmitting} 
            />
            <Select 
              label="Role" 
              options={[{ label: 'Judge', value: 'JUDGE' }, { label: 'Clerk', value: 'CLERK' }, { label: 'Advocate', value: 'ADVOCATE' }, { label: 'Admin', value: 'ADMIN' }]} 
              placeholder="Select Role"
              error={errors.role?.message} 
              {...register('role')} 
              disabled={isSubmitting} 
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', alignItems: 'flex-start' }}>
            <div>
              <Input
                label="Password"
                type={showPass ? 'text' : 'password'}
                placeholder="Ã¢â‚¬Â¢Ã¢â‚¬Â¢Ã¢â‚¬Â¢Ã¢â‚¬Â¢Ã¢â‚¬Â¢Ã¢â‚¬Â¢Ã¢â‚¬Â¢Ã¢â‚¬Â¢"
                error={errors.password?.message}
                {...register('password')}
                disabled={isSubmitting}
                rightIcon={
                  <button type="button" onClick={() => setShowPass(!showPass)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#6B7280' }}>
                    {showPass ? 'Ã°Å¸â„¢Ë†' : 'Ã°Å¸â€˜Â'}
                  </button>
                }
              />
              <PasswordStrengthMeter password={watchedPassword} />
            </div>
            
            <Input
              label="Confirm Password"
              type={showPass ? 'text' : 'password'}
              placeholder="Ã¢â‚¬Â¢Ã¢â‚¬Â¢Ã¢â‚¬Â¢Ã¢â‚¬Â¢Ã¢â‚¬Â¢Ã¢â‚¬Â¢Ã¢â‚¬Â¢Ã¢â‚¬Â¢"
              error={errors.confirmPassword?.message}
              {...register('confirmPassword')}
              disabled={isSubmitting}
            />
          </div>

          <div style={{ marginTop: '0.5rem' }}>
            <Checkbox
              label="I accept the Terms of Service and Privacy Policy"
              error={errors.acceptTerms?.message}
              {...register('acceptTerms')}
              disabled={isSubmitting}
            />
          </div>

          <button
            type="submit"
            disabled={isSubmitting}
            style={{
              width: '100%', padding: '0.875rem', background: '#0F1D3A', color: '#FFFFFF',
              border: 'none', borderRadius: 8, fontSize: '0.875rem', fontWeight: 600,
              cursor: isSubmitting ? 'not-allowed' : 'pointer', opacity: isSubmitting ? 0.8 : 1,
              marginTop: '1rem', transition: 'background-color 0.2s', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: 8
            }}
          >
            {isSubmitting ? (
              <><span style={{ display: 'inline-block', width: 16, height: 16, border: '2px solid rgba(255,255,255,0.3)', borderTopColor: '#fff', borderRadius: '50%', animation: 'spin 1s linear infinite' }} /> Provisioning...</>
            ) : 'Create Account'}
          </button>
        </form>

        <div style={{ marginTop: '2rem', textAlign: 'center', fontSize: '0.875rem', color: '#6B7280' }}>
          Already have an account? <Link to={ROUTES.LOGIN} style={{ color: '#1E40AF', fontWeight: 600, textDecoration: 'none' }}>Sign In</Link>
        </div>
        <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
      </AuthLayout>
    </FeatureGuard>
  )
}
export default RegisterPage
