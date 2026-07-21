import { useState } from 'react'
import { Eye, EyeOff } from 'lucide-react'
import { useDraft } from '../../pages/NewCaseWizardContext'
import indianStatesData from '../../../../data/indianStates.json'

const STATE_DISTRICTS: Record<string, string[]> = indianStatesData as Record<string, string[]>
const ALL_STATES = Object.keys(STATE_DISTRICTS).sort()

export function Step3PetitionerDetails({ onNext, onPrevious }: { onNext: () => void, onPrevious: () => void }) {
  const { draft, updateDraft } = useDraft()
  const [showAadhaar, setShowAadhaar] = useState(false)
  
  const districts = STATE_DISTRICTS[draft.petitionerState || ''] || []

  const maxDobObj = new Date()
  maxDobObj.setFullYear(maxDobObj.getFullYear() - 1)
  const maxDob = maxDobObj.toISOString().split('T')[0]

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
        <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">3</div>
        <h2 className="text-xl font-bold text-gray-800">Petitioner Details</h2>
      </div>

      <div className="flex gap-4 border-b border-gray-200 mb-6">
        <button type="button" className="px-6 py-2 bg-blue-600 text-white font-medium rounded-t-lg">Single Petitioner</button>
        <button type="button" className="px-6 py-2 text-gray-600 font-medium hover:text-blue-600 transition-colors">Multiple Petitioners</button>
      </div>

      <div className="flex flex-col gap-6">
        <div>
          <h3 className="text-sm font-bold text-gray-800 mb-4 border-b pb-2">Personal Details</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-5">
            <div className="flex flex-col gap-1">
              <label className="text-xs font-semibold text-gray-700">Full Name <span className="text-red-500">*</span></label>
              <input type="text" required className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" placeholder="e.g. Ramesh Kumar" value={draft.petitionerName || ''} onChange={e => updateDraft({ petitionerName: e.target.value })} />
            </div>
            <div className="flex flex-col gap-1">
              <label className="text-xs font-semibold text-gray-700">Alias Name</label>
              <input type="text" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" placeholder="e.g. R. Kumar" value={draft.petitionerAlias || ''} onChange={e => updateDraft({ petitionerAlias: e.target.value })} />
            </div>
            
            <div className="flex flex-col gap-1">
              <label className="text-xs font-semibold text-gray-700">Gender <span className="text-red-500">*</span></label>
              <div className="flex items-center gap-6 mt-1">
                <label className="flex items-center gap-2 text-sm">
                  <input type="radio" name="pGender" required value="Male" checked={draft.petitionerGender === 'Male'} onChange={e => updateDraft({ petitionerGender: e.target.value })} className="w-4 h-4 text-blue-600 focus:ring-blue-500" />
                  Male
                </label>
                <label className="flex items-center gap-2 text-sm">
                  <input type="radio" name="pGender" required value="Female" checked={draft.petitionerGender === 'Female'} onChange={e => updateDraft({ petitionerGender: e.target.value })} className="w-4 h-4 text-blue-600 focus:ring-blue-500" />
                  Female
                </label>
                <label className="flex items-center gap-2 text-sm">
                  <input type="radio" name="pGender" required value="Other" checked={draft.petitionerGender === 'Other'} onChange={e => updateDraft({ petitionerGender: e.target.value })} className="w-4 h-4 text-blue-600 focus:ring-blue-500" />
                  Other
                </label>
              </div>
            </div>
            <div className="flex gap-4">
              <div className="flex flex-col gap-1 w-1/2">
                <label className="text-xs font-semibold text-gray-700">DOB <span className="text-red-500">*</span></label>
                <input type="date" required max={maxDob} className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" value={draft.petitionerDob || ''} onChange={e => updateDraft({ petitionerDob: e.target.value })} />
              </div>
              <div className="flex flex-col gap-1 w-1/2">
                <label className="text-xs font-semibold text-gray-700">Age <span className="text-red-500">*</span></label>
                <div className="flex items-center gap-2">
                  <input type="number" required min={1} max={250} className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 w-full" placeholder="e.g. 38" value={draft.petitionerAge || ''} onChange={e => updateDraft({ petitionerAge: e.target.value ? Number(e.target.value) : undefined })} />
                  <span className="text-sm text-gray-600">Years</span>
                </div>
              </div>
            </div>

            <div className="flex flex-col gap-1">
              <label className="text-xs font-semibold text-gray-700">Occupation <span className="text-red-500">*</span></label>
              <input type="text" required className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" placeholder="e.g. Business" value={draft.petitionerOccupation || ''} onChange={e => updateDraft({ petitionerOccupation: e.target.value })} />
            </div>
            <div className="flex flex-col gap-1 relative">
              <label className="text-xs font-semibold text-gray-700">Aadhaar <span className="text-red-500">*</span></label>
              <div className="relative">
                <input 
                  type={showAadhaar ? "text" : "password"} 
                  required 
                  pattern="\d{12}" 
                  maxLength={12}
                  title="Aadhaar must contain exactly 12 digits"
                  className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 w-full pr-10" 
                  placeholder="12 digit Aadhaar Number" 
                  value={draft.petitionerAadhaar || ''} 
                  onChange={e => updateDraft({ petitionerAadhaar: e.target.value })} 
                />
                <button type="button" onClick={() => setShowAadhaar(!showAadhaar)} className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700">
                  {showAadhaar ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
            </div>

            <div className="flex flex-col gap-1">
              <label className="text-xs font-semibold text-gray-700">PAN</label>
              <input type="text" pattern="[A-Z]{5}[0-9]{4}[A-Z]{1}" title="PAN must be in format ABCDE1234F" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 uppercase" placeholder="e.g. ABCDE1234F" value={draft.petitionerPan || ''} onChange={e => updateDraft({ petitionerPan: e.target.value.toUpperCase() })} />
            </div>
            <div className="flex gap-4">
              <div className="flex flex-col gap-1 w-1/2">
                <label className="text-xs font-semibold text-gray-700">Mobile <span className="text-red-500">*</span></label>
                <input type="tel" required pattern="\d{10}" maxLength={10} title="Mobile number must contain exactly 10 digits" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" placeholder="10 digit mobile" value={draft.petitionerMobile || ''} onChange={e => updateDraft({ petitionerMobile: e.target.value })} />
              </div>
              <div className="flex flex-col gap-1 w-1/2">
                <label className="text-xs font-semibold text-gray-700">Email <span className="text-red-500">*</span></label>
                <input type="email" required className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" placeholder="e.g. ramesh@gmail.com" value={draft.petitionerEmail || ''} onChange={e => updateDraft({ petitionerEmail: e.target.value })} />
              </div>
            </div>
          </div>
        </div>

        <div>
          <h3 className="text-sm font-bold text-gray-800 mb-4 border-b pb-2">Address</h3>
          <div className="flex flex-col gap-1 mb-5">
            <label className="text-xs font-semibold text-gray-700">Address <span className="text-red-500">*</span></label>
            <textarea rows={2} required className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 resize-none" placeholder="e.g. 123, Civil Lines" value={draft.petitionerAddress || ''} onChange={e => updateDraft({ petitionerAddress: e.target.value })} />
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-x-6 gap-y-5">
            <div className="flex flex-col gap-1">
              <label className="text-xs font-semibold text-gray-700">State <span className="text-red-500">*</span></label>
              <select required className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" value={draft.petitionerState || ''} onChange={e => updateDraft({ petitionerState: e.target.value, petitionerDistrict: '' })}>
                <option value="">Select State</option>
                {ALL_STATES.map(state => (
                  <option key={state} value={state}>{state}</option>
                ))}
              </select>
            </div>
            <div className="flex flex-col gap-1">
              <label className="text-xs font-semibold text-gray-700">District <span className="text-red-500">*</span></label>
              <select required disabled={!draft.petitionerState} className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 disabled:bg-gray-100" value={draft.petitionerDistrict || ''} onChange={e => updateDraft({ petitionerDistrict: e.target.value })}>
                <option value="">Select District</option>
                {districts.map(district => (
                  <option key={district} value={district}>{district}</option>
                ))}
              </select>
            </div>
            <div className="flex flex-col gap-1">
              <label className="text-xs font-semibold text-gray-700">Pincode <span className="text-red-500">*</span></label>
              <input type="text" required pattern="\d{6}" maxLength={6} title="Pincode must contain exactly 6 digits" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" placeholder="e.g. 226001" value={draft.petitionerPincode || ''} onChange={e => updateDraft({ petitionerPincode: e.target.value })} />
            </div>
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
