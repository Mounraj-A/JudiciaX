// â”€â”€â”€ PasswordStrengthMeter â€” Phase F5 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

export interface PasswordStrengthMeterProps {
  password: string
}

export function PasswordStrengthMeter({ password }: PasswordStrengthMeterProps) {
  // Score from 0 to 4
  const calculateStrength = (pwd: string) => {
    let score = 0
    if (!pwd) return score
    if (pwd.length >= 8) score += 1
    if (/[A-Z]/.test(pwd)) score += 1
    if (/[0-9]/.test(pwd)) score += 1
    if (/[^A-Za-z0-9]/.test(pwd)) score += 1
    return score
  }

  const score = calculateStrength(password)
  const getColors = () => {
    if (score === 0) return ['#E5E7EB', '#E5E7EB', '#E5E7EB', '#E5E7EB']
    if (score === 1) return ['#EF4444', '#E5E7EB', '#E5E7EB', '#E5E7EB'] // Weak
    if (score === 2) return ['#F59E0B', '#F59E0B', '#E5E7EB', '#E5E7EB'] // Fair
    if (score === 3) return ['#10B981', '#10B981', '#10B981', '#E5E7EB'] // Good
    return ['#059669', '#059669', '#059669', '#059669'] // Strong
  }
  const colors = getColors()

  const labels = ['Very Weak', 'Weak', 'Fair', 'Good', 'Strong']

  const requirements = [
    { label: 'At least 8 characters', met: password.length >= 8 },
    { label: 'One uppercase letter', met: /[A-Z]/.test(password) },
    { label: 'One number', met: /[0-9]/.test(password) },
    { label: 'One special character', met: /[^A-Za-z0-9]/.test(password) }
  ]

  return (
    <div style={{ marginTop: '0.75rem' }}>
      <div style={{ display: 'flex', gap: '0.25rem', marginBottom: '0.5rem' }}>
        {colors.map((bg, idx) => (
          <div key={idx} style={{ height: 4, flex: 1, backgroundColor: bg, borderRadius: 2, transition: 'background-color 0.3s ease' }} />
        ))}
      </div>
      <div style={{ fontSize: '0.75rem', fontWeight: 600, color: score < 2 ? '#EF4444' : score === 2 ? '#F59E0B' : '#10B981', marginBottom: '0.75rem' }}>
        Password Strength: {labels[score]}
      </div>
      <ul style={{ margin: 0, padding: 0, listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.375rem' }}>
        {requirements.map((req, idx) => (
          <li key={idx} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.75rem', color: req.met ? '#10B981' : '#6B7280', transition: 'color 0.2s' }}>
            <span style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', width: 14, height: 14, borderRadius: '50%', background: req.met ? '#10B981' : '#E5E7EB', color: '#FFF', fontSize: '0.5rem' }}>
              {req.met ? 'âœ“' : ''}
            </span>
            {req.label}
          </li>
        ))}
      </ul>
    </div>
  )
}
