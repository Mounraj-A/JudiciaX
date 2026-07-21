// ─── JudicialNotesPanel — Phase F9 ───────────────────────────────────────────
import { useState } from 'react'
import { Section } from '@/shared/components/layout'
import { Button } from '@/shared/design-system/components/primitives/Button'

export function JudicialNotesPanel() {
  const [note, setNote] = useState('')

  return (
    <Section title="Private Judicial Notes" subtitle="Drafts & observations (Not visible to other roles)" bordered padded>
      <div className="flex flex-col gap-3">
        <textarea
          className="w-full min-h-[200px] p-3 text-sm rounded-md border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-900 focus:ring-2 focus:ring-brand-500 focus:border-brand-500 resize-y"
          placeholder="Enter draft judgment notes, case observations, or reminders here..."
          value={note}
          onChange={(e) => setNote(e.target.value)}
        />
        <div className="flex justify-between items-center">
          <span className="text-xs text-slate-500">Auto-saved just now</span>
          <div className="flex gap-2">
            <Button variant="outline" size="sm">Pin Note</Button>
            <Button variant="primary" size="sm">Save to Drafts</Button>
          </div>
        </div>
      </div>
    </Section>
  )
}
