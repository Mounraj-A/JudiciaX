// â”€â”€â”€ AdvocateProfilePage â€” Phase F7 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import { useEffect } from 'react'
import { PageHeader } from '@/shared/components/layout'
import { Input } from '@/shared/components/form'
import { EntityAvatar } from '@/shared/components/badges'
import { useAdvocateProfile, useUpdateAdvocateProfile } from '@/features/advocate/api/useAdvocateProfile'
import { useForm, Controller } from 'react-hook-form'
import { UpdateAdvocateProfileRequest } from '@/api/services/advocate/advocateApi'
import { useAuthContext } from '@/features/auth/contexts/AuthContext'

export function AdvocateProfilePage() {
  const { data: profile, isLoading, error } = useAdvocateProfile()
  const { mutate: updateProfile, isPending } = useUpdateAdvocateProfile()
  const { logout } = useAuthContext()

  const { control, handleSubmit, reset } = useForm<UpdateAdvocateProfileRequest>({
    defaultValues: {
      barCouncilNumber: '',
      stateBarCouncil: '',
      lawFirm: '',
      specialization: '',
      yearsOfPractice: 0,
      officeAddress: '',
      officeCity: '',
      officeState: '',
      officePincode: ''
    }
  })

  useEffect(() => {
    if (profile) {
      reset({
        barCouncilNumber: profile.barCouncilNumber || '',
        stateBarCouncil: profile.stateBarCouncil || '',
        lawFirm: profile.lawFirm || '',
        specialization: profile.specialization || '',
        yearsOfPractice: profile.yearsOfPractice || 0,
        officeAddress: profile.officeAddress || '',
        officeCity: profile.officeCity || '',
        officeState: profile.officeState || '',
        officePincode: profile.officePincode || ''
      })
    }
  }, [profile, reset])

  const onSubmit = (data: UpdateAdvocateProfileRequest) => {
    updateProfile(data)
  }

  if (isLoading) return <div style={{ padding: '2rem' }}>Loading profile...</div>
  if (error || !profile) return <div style={{ padding: '2rem', color: 'red' }}>Failed to load profile.</div>

  return (
    <form onSubmit={handleSubmit(onSubmit)} style={{ padding: '2rem', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <PageHeader 
        title="Profile & Settings" 
        description="Manage your personal details, professional credentials, and security preferences." 
        actions={
          <button 
            type="submit" 
            disabled={isPending}
            style={{ 
              padding: '0.5rem 1rem', background: '#0F1D3A', color: '#FFF', 
              borderRadius: '8px', border: 'none', fontSize: '0.875rem', 
              fontWeight: 600, cursor: isPending ? 'not-allowed' : 'pointer',
              opacity: isPending ? 0.7 : 1 
            }}>
            {isPending ? 'Saving...' : 'Save Changes'}
          </button>
        }
      />

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '2rem' }}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E5E7EB' }}>
            <h2 style={{ margin: '0 0 1.5rem 0', fontSize: '1.25rem' }}>Personal Information</h2>
            <div style={{ display: 'flex', gap: '1.5rem', marginBottom: '2rem' }}>
              <EntityAvatar name={profile.fullName || 'Advocate'} size={80} />
              <div style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', gap: '0.5rem' }}>
                <span style={{ fontSize: '1rem', fontWeight: 600 }}>{profile.fullName}</span>
                <span style={{ fontSize: '0.875rem', color: '#6B7280' }}>Verification: {profile.verificationStatus}</span>
              </div>
            </div>
            
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <Input label="Full Name" value={profile.fullName} disabled />
              <Input label="Email Address" value={profile.email} type="email" disabled />
              <Input label="Phone Number" value={profile.phoneNumber} disabled />
            </div>
          </div>
          
          <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E5E7EB' }}>
            <h2 style={{ margin: '0 0 1.5rem 0', fontSize: '1.25rem' }}>Office Address</h2>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <Controller
                name="officeAddress"
                control={control}
                render={({ field }) => <Input label="Office Address" {...field} />}
              />
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <Controller
                  name="officeCity"
                  control={control}
                  render={({ field }) => <Input label="City" {...field} />}
                />
                <Controller
                  name="officeState"
                  control={control}
                  render={({ field }) => <Input label="State" {...field} />}
                />
              </div>
              <Controller
                name="officePincode"
                control={control}
                render={({ field }) => <Input label="Pincode" {...field} />}
              />
            </div>
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E5E7EB' }}>
            <h2 style={{ margin: '0 0 1.5rem 0', fontSize: '1.25rem' }}>Professional Details</h2>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <Controller
                name="barCouncilNumber"
                control={control}
                render={({ field }) => <Input label="Bar Registration Number" {...field} />}
              />
              <Controller
                name="stateBarCouncil"
                control={control}
                render={({ field }) => <Input label="State Bar Council" {...field} />}
              />
              <Controller
                name="lawFirm"
                control={control}
                render={({ field }) => <Input label="Law Firm / Chambers" {...field} />}
              />
              <Controller
                name="specialization"
                control={control}
                render={({ field }) => <Input label="Specialization" {...field} />}
              />
              <Controller
                name="yearsOfPractice"
                control={control}
                render={({ field }) => <Input label="Years of Practice" type="number" {...field} />}
              />
            </div>
          </div>

          <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E5E7EB' }}>
            <h2 style={{ margin: '0 0 1.5rem 0', fontSize: '1.25rem' }}>Security</h2>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <button type="button" style={{ width: '100%', padding: '0.75rem 1rem', background: '#F9FAFB', border: '1px solid #E5E7EB', borderRadius: '8px', fontSize: '0.875rem', fontWeight: 600, color: '#374151', cursor: 'pointer', textAlign: 'left' }}>Change Password</button>
              <button type="button" style={{ width: '100%', padding: '0.75rem 1rem', background: '#F9FAFB', border: '1px solid #E5E7EB', borderRadius: '8px', fontSize: '0.875rem', fontWeight: 600, color: '#374151', cursor: 'pointer', textAlign: 'left' }}>Two-Factor Authentication (Enabled)</button>
              <button 
                type="button" 
                onClick={() => logout().catch(() => {})}
                style={{ width: '100%', padding: '0.75rem 1rem', background: '#FEF2F2', border: '1px solid #FECACA', borderRadius: '8px', fontSize: '0.875rem', fontWeight: 600, color: '#DC2626', cursor: 'pointer', textAlign: 'center', marginTop: '1rem' }}>
                Sign Out
              </button>
            </div>
          </div>
        </div>
      </div>
    </form>
  )
}
export default AdvocateProfilePage
