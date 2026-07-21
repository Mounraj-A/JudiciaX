
import { useDraft } from '../../pages/NewCaseWizardContext'
import { useCaseCategoriesByType } from '../../../../api/services/master/useMaster'

export function Step2CaseInfo({ onNext, onPrevious }: { onNext: () => void, onPrevious: () => void }) {
  const { draft, updateDraft } = useDraft()
  const { data: caseCategories, isLoading: loadingCategories } = useCaseCategoriesByType(draft.caseType)

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
        <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">2</div>
        <h2 className="text-xl font-bold text-gray-800">Case Information</h2>
      </div>

      {/* Form Grid */}
      <div className="flex flex-col gap-5">
        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Case Title <span className="text-red-500">*</span></label>
          <input 
            type="text" 
            required 
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" 
            placeholder="e.g. Suit for Recovery of Money" 
            value={draft.caseTitle || ''}
            onChange={(e) => updateDraft({ caseTitle: e.target.value })}
          />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Cause Title <span className="text-red-500">*</span></label>
          <input 
            type="text" 
            required 
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" 
            placeholder="e.g. Ramesh Kumar Vs. Suresh Singh" 
            value={draft.causeTitle || ''}
            onChange={(e) => updateDraft({ causeTitle: e.target.value })}
          />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Subject <span className="text-red-500">*</span></label>
          <input 
            type="text" 
            required 
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" 
            placeholder="e.g. Recovery of Loan Amount" 
            value={draft.subject || ''}
            onChange={(e) => updateDraft({ subject: e.target.value })}
          />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-5">
          <div className="flex flex-col gap-1">
            <label className="text-xs font-semibold text-gray-700">Case Category <span className="text-red-500">*</span></label>
            <select 
              required
              className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 disabled:bg-gray-100"
              value={draft.caseCategoryUuid || ''}
              onChange={(e) => updateDraft({ caseCategoryUuid: e.target.value })}
              disabled={!draft.caseType || loadingCategories}
            >
              <option value="">{loadingCategories ? 'Loading...' : 'Select Case Category'}</option>
              {caseCategories?.map(cc => (
                <option key={cc.uuid} value={cc.uuid}>{cc.categoryName}</option>
              ))}
            </select>
          </div>
          <div className="flex flex-col gap-1">
            <label className="text-xs font-semibold text-gray-700">Nature of Suit <span className="text-red-500">*</span></label>
            <input 
              type="text" 
              required 
              className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" 
              placeholder="e.g. Recovery of Loan" 
              value={draft.natureOfSuit || ''}
              onChange={(e) => updateDraft({ natureOfSuit: e.target.value })}
            />
          </div>
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Relief Sought <span className="text-red-500">*</span></label>
          <textarea 
            rows={2} 
            required 
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 resize-none" 
            placeholder="e.g. Recovery of Rs. 5,00,000 with interest" 
            value={draft.reliefSought || ''}
            onChange={(e) => updateDraft({ reliefSought: e.target.value })}
          />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Short Description <span className="text-red-500">*</span></label>
          <input 
            type="text" 
            required 
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" 
            placeholder="e.g. Loan not repaid despite multiple requests." 
            value={draft.shortDescription || ''}
            onChange={(e) => updateDraft({ shortDescription: e.target.value })}
          />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Detailed Description <span className="text-red-500">*</span></label>
          <textarea 
            rows={3} 
            required 
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 resize-none" 
            placeholder="e.g. The respondent has failed to repay the loan amount of Rs. 5,00,000 taken on 01-01-2024. Legal notice was sent but no response received." 
            value={draft.detailedDescription || ''}
            onChange={(e) => updateDraft({ detailedDescription: e.target.value })}
          />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-5">
          <div className="flex flex-col gap-1">
            <label className="text-xs font-semibold text-gray-700">Cause of Action <span className="text-red-500">*</span></label>
            <input 
              type="text" 
              required 
              className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" 
              placeholder="e.g. Loan Agreement dated 01-01-2024" 
              value={draft.causeOfAction || ''}
              onChange={(e) => updateDraft({ causeOfAction: e.target.value })}
            />
          </div>
          <div className="flex flex-col gap-1">
            <label className="text-xs font-semibold text-gray-700">Date of Cause of Action <span className="text-red-500">*</span></label>
            <input 
              type="date" 
              required 
              className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" 
              value={draft.dateOfCauseOfAction || ''}
              onChange={(e) => updateDraft({ dateOfCauseOfAction: e.target.value })}
            />
          </div>
        </div>
      </div>

      {/* Footer / Actions */}
      <div className="flex justify-between mt-10">
        <button 
          type="button"
          onClick={onPrevious}
          className="px-8 py-2 border border-blue-600 text-blue-600 rounded font-medium hover:bg-blue-50 transition-colors shadow-sm"
        >
          Previous
        </button>
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
