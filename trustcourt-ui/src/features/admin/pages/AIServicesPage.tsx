// ─── AIServicesPage — Phase F10 ──────────────────────────────────────────────
import React from 'react'
import { PageHeader, ContentContainer, Section, GridLayout, CardSection } from '@/shared/components/layout'
import { Button } from '@/shared/design-system/components/primitives/Button'
import { StatusBadge, ProgressBadge } from '@/shared/components/badges'
import { useAdminAISettings, useAdminAIUsage, useAIGatewayHealth, useToggleAI, useSetModelVersion, useSetPriorityThreshold, useSetConfidenceThreshold, useSetExplainability } from '../hooks/useAdminAI'
import { LoadingLayout, ErrorLayout } from '@/shared/components/layout/StateLayouts'
import { toast } from 'react-hot-toast'

export function AIServicesPage() {
  const { data: settings, isLoading: isLoadingSettings, error: errorSettings, refetch: refetchSettings } = useAdminAISettings()
  const { data: usage, isLoading: isLoadingUsage } = useAdminAIUsage()
  const { data: health } = useAIGatewayHealth()

  const toggleAIMutation = useToggleAI()
  const modelMutation = useSetModelVersion()
  const priorityMutation = useSetPriorityThreshold()
  const confidenceMutation = useSetConfidenceThreshold()
  const explainabilityMutation = useSetExplainability()

  if (isLoadingSettings || isLoadingUsage) return <LoadingLayout message="Loading AI Services…" />
  if (errorSettings) return <ErrorLayout message={errorSettings.message} onRetry={refetchSettings} />
  if (!settings) return null

  const handleToggleAI = async () => {
    try {
      await toggleAIMutation.mutateAsync(!settings.aiEnabled)
      toast.success(`AI Engine ${!settings.aiEnabled ? 'Enabled' : 'Disabled'}`)
    } catch (err: any) {
      toast.error(err.message)
    }
  }

  const handleToggleExplainability = async () => {
    try {
      await explainabilityMutation.mutateAsync(!settings.explainabilityEnabled)
      toast.success(`Explainability ${!settings.explainabilityEnabled ? 'Enabled' : 'Disabled'}`)
    } catch (err: any) {
      toast.error(err.message)
    }
  }

  const handleModelChange = async (e: React.ChangeEvent<HTMLSelectElement>) => {
    try {
      await modelMutation.mutateAsync(e.target.value)
      toast.success('Model version updated')
    } catch (err: any) {
      toast.error(err.message)
    }
  }

  return (
    <ContentContainer>
      <PageHeader
        title="AI Control Center"
        subtitle="Manage the TrustCourt AI Engine, adjust thresholds, and monitor performance."
        actions={
          <Button
            variant={settings.aiEnabled ? 'danger' : 'primary'}
            onClick={handleToggleAI}
            disabled={toggleAIMutation.isPending}
          >
            {toggleAIMutation.isPending ? 'Processing…' : (settings.aiEnabled ? 'Disable Global AI' : 'Enable Global AI')}
          </Button>
        }
      />

      <GridLayout cols={3}>
        <Section title="AI Settings">
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            <div>
              <label style={{ fontSize: '0.875rem', fontWeight: 500, color: '#374151', display: 'block', marginBottom: '0.5rem' }}>Active Model</label>
              <select
                value={settings.modelVersion}
                onChange={handleModelChange}
                disabled={modelMutation.isPending || !settings.aiEnabled}
                style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }}
              >
                <option value="trustcourt-v1.0">TrustCourt Core v1.0</option>
                <option value="trustcourt-v1.1-beta">TrustCourt Enhanced v1.1 (Beta)</option>
              </select>
            </div>
            
            <div>
              <label style={{ fontSize: '0.875rem', fontWeight: 500, color: '#374151', display: 'block', marginBottom: '0.5rem' }}>
                Priority Threshold: {settings.priorityThreshold}%
              </label>
              <input
                type="range"
                min="0" max="100"
                value={settings.priorityThreshold}
                onChange={(e) => priorityMutation.mutate(Number(e.target.value))}
                disabled={!settings.aiEnabled}
                style={{ width: '100%' }}
              />
              <p style={{ fontSize: '0.75rem', color: '#6B7280', marginTop: '0.25rem' }}>Minimum confidence to assign high priority to a case.</p>
            </div>

            <div>
              <label style={{ fontSize: '0.875rem', fontWeight: 500, color: '#374151', display: 'block', marginBottom: '0.5rem' }}>
                Confidence Threshold: {settings.confidenceThreshold}%
              </label>
              <input
                type="range"
                min="0" max="100"
                value={settings.confidenceThreshold}
                onChange={(e) => confidenceMutation.mutate(Number(e.target.value))}
                disabled={!settings.aiEnabled}
                style={{ width: '100%' }}
              />
              <p style={{ fontSize: '0.75rem', color: '#6B7280', marginTop: '0.25rem' }}>Minimum confidence to auto-approve documents.</p>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0.75rem', background: '#F9FAFB', borderRadius: '0.375rem', border: '1px solid #E5E7EB' }}>
              <div>
                <div style={{ fontSize: '0.875rem', fontWeight: 500 }}>Explainable AI (XAI)</div>
                <div style={{ fontSize: '0.75rem', color: '#6B7280' }}>Generate reasoning for AI decisions</div>
              </div>
              <input
                type="checkbox"
                checked={settings.explainabilityEnabled}
                onChange={handleToggleExplainability}
                disabled={explainabilityMutation.isPending || !settings.aiEnabled}
                style={{ width: '1.25rem', height: '1.25rem' }}
              />
            </div>
          </div>
        </Section>

        <Section title="Gateway Health" className="col-span-2">
          {health ? (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1rem', background: health.status === 'UP' ? '#F0FDF4' : '#FEF2F2', border: `1px solid ${health.status === 'UP' ? '#BBF7D0' : '#FCA5A5'}`, borderRadius: '0.5rem' }}>
                <div>
                  <div style={{ fontSize: '1.125rem', fontWeight: 600, color: health.status === 'UP' ? '#166534' : '#991B1B' }}>Gateway {health.status}</div>
                  <div style={{ fontSize: '0.875rem', color: health.status === 'UP' ? '#15803D' : '#B91C1C' }}>Version: {health.version} | Uptime: {Math.floor(health.uptime / 60)}m</div>
                </div>
                <StatusBadge status={health.status === 'UP' ? 'success' : 'danger'} label={health.status} />
              </div>
              
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                {Object.entries(health.services || {}).map(([service, details]) => (
                  <div key={service} style={{ padding: '0.75rem', border: '1px solid #E5E7EB', borderRadius: '0.375rem' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                      <span style={{ fontSize: '0.875rem', fontWeight: 500, textTransform: 'capitalize' }}>{service}</span>
                      <StatusBadge status={details.status === 'UP' ? 'success' : 'danger'} label={details.status} />
                    </div>
                    <div style={{ fontSize: '0.75rem', color: '#6B7280' }}>Latency: {details.latencyMs}ms</div>
                  </div>
                ))}
              </div>
            </div>
          ) : (
            <div style={{ padding: '2rem', textAlign: 'center', color: '#6B7280' }}>Checking health...</div>
          )}
        </Section>
      </GridLayout>

      <GridLayout cols={4} className="mt-6">
        <CardSection title="Total Requests" className="text-center">
          <div style={{ fontSize: '2rem', fontWeight: 700, color: '#111827' }}>{usage?.totalRequests?.toLocaleString() || 0}</div>
        </CardSection>
        <CardSection title="Success Rate" className="text-center">
          <div style={{ fontSize: '2rem', fontWeight: 700, color: '#059669' }}>
            {usage && usage.totalRequests > 0 ? Math.round((usage.successfulRequests / usage.totalRequests) * 100) : 0}%
          </div>
        </CardSection>
        <CardSection title="Avg Latency" className="text-center">
          <div style={{ fontSize: '2rem', fontWeight: 700, color: '#3B82F6' }}>{usage?.avgLatencyMs || 0}ms</div>
        </CardSection>
        <CardSection title="Requests Today" className="text-center">
          <div style={{ fontSize: '2rem', fontWeight: 700, color: '#8B5CF6' }}>{usage?.requestsToday?.toLocaleString() || 0}</div>
        </CardSection>
      </GridLayout>
    </ContentContainer>
  )
}
