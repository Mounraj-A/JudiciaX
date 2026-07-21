import React from 'react'
import { useCaseTypes } from '../../../../api/services/master/useMaster'
import { useDraft } from '../../pages/NewCaseWizardContext'
import indianStatesData from '../../../../data/indianStates.json'

const STATE_DISTRICTS: Record<string, string[]> = indianStatesData as Record<string, string[]>
const ALL_STATES = Object.keys(STATE_DISTRICTS).sort()

const DISTRICT_COURTS: Record<string, string[]> = {
  "Chennai": ["Madras High Court", "City Civil Court", "Family Court Chennai"],
  "Mumbai": ["Bombay High Court", "City Civil and Sessions Court", "Family Court Mumbai"],
  "Lucknow": ["Allahabad High Court (Lucknow Bench)", "District & Sessions Court Lucknow"],
  "Bengaluru": ["Karnataka High Court", "City Civil Court Bengaluru", "Family Court Bengaluru"]
}

export function Step1WhereToFile({ onNext }: { onNext: () => void }) {
  const { draft, updateDraft } = useDraft()
  const { data: caseTypes, isLoading: loadingTypes } = useCaseTypes()
  
  const districts = STATE_DISTRICTS[draft.state || ''] || []
  const courts = DISTRICT_COURTS[draft.district || ''] || (draft.district ? ["District Court", "Sessions Court", "Family Court"] : [])

  return (
    <form 
      className="bg-white rounded-lg border border-gray-200 shadow-sm p-8"
      onSubmit={(e) => {
        e.preventDefault()
        onNext()
      }}
    >
      {/* Header */}
      <div className="flex items-center gap-3 mb-8">
        <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">1</div>
        <h2 className="text-xl font-bold text-gray-800">Where to File</h2>
      </div>

      {/* Form Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-6">
        {/* Row 1 */}
        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">State <span className="text-red-500">*</span></label>
          <select 
            required
            value={draft.state || ''} 
            onChange={(e) => {
              updateDraft({ 
                state: e.target.value, 
                district: '', 
                court: '' 
              })
            }}
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500"
          >
            <option value="">Select State</option>
            {ALL_STATES.map(state => (
              <option key={state} value={state}>{state}</option>
            ))}
          </select>
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">District <span className="text-red-500">*</span></label>
          <select 
            required
            value={draft.district || ''}
            onChange={(e) => {
              updateDraft({
                district: e.target.value,
                court: ''
              })
            }}
            disabled={!draft.state}
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 disabled:bg-gray-100"
          >
            <option value="">Select District</option>
            {districts.map(district => (
              <option key={district} value={district}>{district}</option>
            ))}
          </select>
        </div>

        {/* Row 2 */}
        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Court <span className="text-red-500">*</span></label>
          <select 
            required
            value={draft.court || ''}
            onChange={(e) => updateDraft({ court: e.target.value })}
            disabled={!draft.district}
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 disabled:bg-gray-100"
          >
            <option value="">Select Court</option>
            {courts.map(court => (
              <option key={court} value={court}>{court}</option>
            ))}
          </select>
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Court Type <span className="text-red-500">*</span></label>
          <select 
            required 
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500"
            value={draft.courtType || ''}
            onChange={(e) => updateDraft({ courtType: e.target.value })}
          >
            <option value="">Select Court Type</option>
            <option value="criminal">Criminal Court</option>
            <option value="civil">Civil Court</option>
          </select>
        </div>

        {/* Row 3 */}
        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Bench <span className="text-red-500">*</span></label>
          <select 
            required 
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500"
            value={draft.bench || ''}
            onChange={(e) => updateDraft({ bench: e.target.value })}
          >
            <option value="">Select Bench</option>
            <option>Single Bench</option>
            <option>Division Bench</option>
            <option>Full Bench</option>
            <option>Constitutional Bench</option>
          </select>
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Court Hall</label>
          <select 
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500"
            value={draft.courtHall || ''}
            onChange={(e) => updateDraft({ courtHall: e.target.value })}
          >
            <option value="">Select Court Hall</option>
            <option>Court Hall 1</option>
            <option>Court Hall 2</option>
            <option>Court Hall 3</option>
            <option>Court Hall 4</option>
          </select>
        </div>

        {/* Row 4 */}
        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Filing Mode <span className="text-red-500">*</span></label>
          <select 
            required 
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500"
            value={draft.filingMode || ''}
            onChange={(e) => updateDraft({ filingMode: e.target.value })}
          >
            <option value="">Select Filing Mode</option>
            <option>Fresh</option>
            <option>Appeal</option>
            <option>Revision</option>
            <option>Review</option>
            <option>Transfer</option>
          </select>
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Case Type <span className="text-red-500">*</span></label>
          <select 
            required
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 disabled:bg-gray-100"
            value={draft.caseType || ''}
            onChange={(e) => {
              updateDraft({ caseType: e.target.value, caseCategoryUuid: '' })
            }}
            disabled={loadingTypes}
          >
            <option value="">{loadingTypes ? 'Loading...' : 'Select Case Type'}</option>
            {caseTypes?.map(ct => (
              <option key={ct.uuid} value={ct.uuid}>{ct.typeName}</option>
            ))}
          </select>
        </div>


      </div>

      {/* Footer / Actions */}
      <div className="flex justify-end mt-10">
        <button 
          type="submit"
          className="px-8 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors shadow-sm"
        >
          Next
        </button>
      </div>
    </form>
  )
}
