// ─── DecisionActionPanel — Phase F9 ──────────────────────────────────────────
import { Section } from '@/shared/components/layout'
import { Button } from '@/shared/design-system/components/primitives/Button'

export function DecisionActionPanel() {
  return (
    <Section title="Judicial Decision" subtitle="Finalize workflow actions" bordered padded>
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
        <Button variant="primary" className="w-full">Reserve Judgment</Button>
        <Button variant="outline" className="w-full">Dispose Case</Button>
        <Button variant="outline" className="w-full">Adjourn</Button>
        <Button variant="outline" className="w-full">Schedule Hearing</Button>
        
        <Button variant="ghost" className="w-full text-slate-600">Request Documents</Button>
        <Button variant="ghost" className="w-full text-slate-600">Return for Scrutiny</Button>
        <Button variant="ghost" className="w-full text-slate-600">Transfer Case</Button>
        <Button variant="ghost" className="w-full text-brand-600">Generate Order Draft</Button>
      </div>
    </Section>
  )
}
