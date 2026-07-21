import { useState, useEffect } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useAuthContext } from '@/features/auth/contexts/AuthContext';
import { FeatureGuard } from '@/core/guards/FeatureGuard';
import { isAuthError } from '@/features/auth/errors/AuthError';
import { ROUTES } from '@/constants/routes';
import { Scale, Eye, EyeOff, ArrowLeft } from 'lucide-react';
import './LoginPage.css';

// ─── Zod Schema ───
const loginSchema = z.object({
  email: z.string().email('Please enter a valid email address.'),
  password: z.string().min(1, 'Password is required.'),
  rememberMe: z.boolean().optional(),
});
type LoginFormValues = z.infer<typeof loginSchema>;

export function LoginPage() {
  const { login, isAuthenticated, user } = useAuthContext();
  const navigate = useNavigate();
  const location = useLocation();
  const [error, setError] = useState('');
  const [showPass, setShowPass] = useState(false);
  
  const from = (location.state as { from?: { pathname: string } })?.from?.pathname;

  const { register, handleSubmit, setValue, formState: { errors, isSubmitting } } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: { email: '', password: '', rememberMe: false }
  });

  // Redirect if already authenticated
  useEffect(() => {
    if (isAuthenticated && user?.role) {
      navigate('/gateway', { replace: true, state: { from } });
    }
  }, [isAuthenticated, user, navigate, from]);

  const onSubmit = async (data: LoginFormValues) => {
    setError('');
    try {
      await login(data);
    } catch (err) {
      setError(isAuthError(err) ? err.userMessage : 'Authentication failed. Please check your credentials and try again.');
    }
  };

  const handleDemoClick = (email: string) => {
    setValue('email', email, { shouldValidate: true });
    setValue('password', 'Admin@1234', { shouldValidate: true });
  };

  return (
    <div className="login-page">
      {/* ── Left Panel ── */}
      <div className="login-panel-left">
        <div className="login-brand">
          <div className="login-brand-mark"><Scale size={22} /></div>
          <div>
            <div className="login-brand-name">JudiciaX</div>
            <div className="login-brand-sub">Judicial Case Management</div>
          </div>
        </div>

        <div className="login-panel-left-body">
          <div className="login-eyebrow">Enterprise Access Gateway</div>
          <h2>Access your<br /><span>judicial workspace</span></h2>
          <p>Sign in to TrustCourt to access your personalized dashboard, manage cases, schedules, and digital court records.</p>

          <div className="login-stats">
            <div><span className="num">256-bit</span><span className="lbl">Encryption</span></div>
            <div><span className="num">Role-based</span><span className="lbl">Access Control</span></div>
            <div><span className="num">AI</span><span className="lbl">Powered</span></div>
          </div>
        </div>

        <div className="login-panel-left-footer">
          © {new Date().getFullYear()} JudiciaX Platform
        </div>
      </div>

      {/* ── Right Panel ── */}
      <div className="login-panel-right">
        <div className="login-form-box">
          <h1>Welcome back</h1>
          <p className="login-sub">Sign in to your TrustCourt workspace.</p>

          {/* DEMO CREDENTIALS BOX */}
          <div className="login-demo-box">
            <div className="demo-title">Test Accounts (Password: Admin@1234)</div>
            <div className="login-demo-grid">
              <div className="login-demo-item" onClick={() => handleDemoClick('judge@judiciai.com')}>
                <div className="role">Judge</div>
                <div className="cred">judge@judiciai.com</div>
              </div>
              <div className="login-demo-item" onClick={() => handleDemoClick('clerk@judiciai.com')}>
                <div className="role">Clerk</div>
                <div className="cred">clerk@judiciai.com</div>
              </div>
              <div className="login-demo-item" onClick={() => handleDemoClick('advocate@judiciai.com')}>
                <div className="role">Advocate</div>
                <div className="cred">advocate@judiciai.com</div>
              </div>
              <div className="login-demo-item" onClick={() => handleDemoClick('admin@courtai.com')}>
                <div className="role">Admin</div>
                <div className="cred">admin@courtai.com</div>
              </div>
            </div>
          </div>

          {error && (
            <div className="lp-alert-error">
              <span style={{ fontSize: '1.25rem' }}>⚠</span> {error}
            </div>
          )}

          <form onSubmit={handleSubmit(onSubmit)} noValidate>
            <div className="lp-field">
              <label className="lp-label">Email Address</label>
              <div className="lp-input-wrap">
                <input
                  type="email"
                  className={`lp-input ${errors.email ? 'error' : ''}`}
                  placeholder="e.g., judge@judiciai.com"
                  disabled={isSubmitting}
                  {...register('email')}
                />
              </div>
              {errors.email && <div className="lp-error-msg">{errors.email.message}</div>}
            </div>

            <div className="lp-field">
              <label className="lp-label">Password</label>
              <div className="lp-input-wrap">
                <input
                  type={showPass ? 'text' : 'password'}
                  className={`lp-input ${errors.password ? 'error' : ''}`}
                  placeholder="••••••••"
                  disabled={isSubmitting}
                  {...register('password')}
                />
                <button
                  type="button"
                  className="lp-input-icon"
                  onClick={() => setShowPass(!showPass)}
                  disabled={isSubmitting}
                  title={showPass ? 'Hide password' : 'Show password'}
                >
                  {showPass ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
              {errors.password && <div className="lp-error-msg">{errors.password.message}</div>}
            </div>

            <div className="lp-row">
              <FeatureGuard feature="REMEMBER_ME">
                <label className="lp-remember">
                  <input type="checkbox" disabled={isSubmitting} {...register('rememberMe')} />
                  Remember me
                </label>
              </FeatureGuard>
              <Link to={ROUTES.FORGOT_PASSWORD} className="lp-forgot">
                Forgot password?
              </Link>
            </div>

            <button type="submit" className="lp-submit" disabled={isSubmitting}>
              {isSubmitting ? (
                <>
                  <span className="lp-spinner" />
                  Authenticating...
                </>
              ) : 'Sign In'}
            </button>
          </form>

          <FeatureGuard feature="REGISTRATION">
            <div style={{ marginTop: '24px', textAlign: 'center', fontSize: '13.5px', color: 'var(--text-secondary)' }}>
              Need an account? <Link to={ROUTES.REGISTER ?? '/register'} style={{ color: 'var(--royal)', fontWeight: 500, textDecoration: 'none' }}>Create Account</Link>
            </div>
          </FeatureGuard>

          <div style={{ textAlign: 'center' }}>
            <Link to="/" className="lp-back-link">
              <ArrowLeft size={14} /> Back to JudiciaX Home
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;
