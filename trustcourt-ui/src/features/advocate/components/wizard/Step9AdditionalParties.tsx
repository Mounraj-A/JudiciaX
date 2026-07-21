import { useState } from 'react'
import { FiTrash2, FiPlus, FiEdit2, FiEye } from 'react-icons/fi'
import { useDraft } from '../../pages/NewCaseWizardContext'

const PARTY_TYPES = [
  "Co-Petitioner", "Co-Plaintiff", "Co-Respondent", "Co-Defendant", 
  "Co-Complainant", "Co-Informant", "Co-Accused", "Proforma Respondent", 
  "Proforma Defendant", "Caveator", "Victim", "Witness", "Intervenor", 
  "Amicus Curiae", "Power of Attorney (POA) Holder", "Legal Heir", 
  "Legal Representative", "Guardian ad Litem", "Official Liquidator", 
  "Official Receiver"
]

export function Step9AdditionalParties({ onNext, onPrevious }: { onNext: () => void, onPrevious: () => void }) {
  const { draft, updateDraft } = useDraft()
  const additionalParties = draft.additionalParties || []
  
  const [isFormOpen, setIsFormOpen] = useState(false)
  const [editingId, setEditingId] = useState<string | null>(null)
  const [viewMode, setViewMode] = useState<boolean>(false)
  
  const [formData, setFormData] = useState({
    partyType: '',
    name: '',
    address: '',
    mobile: ''
  })

  const handleOpenForm = () => {
    setFormData({ partyType: '', name: '', address: '', mobile: '' })
    setEditingId(null)
    setViewMode(false)
    setIsFormOpen(true)
  }

  const handleCloseForm = () => {
    setIsFormOpen(false)
    setEditingId(null)
    setViewMode(false)
  }

  const handleEdit = (id: string, isView: boolean = false) => {
    const party = additionalParties.find(p => p.id === id)
    if (party) {
      setFormData({
        partyType: party.partyType,
        name: party.name,
        address: party.address,
        mobile: party.mobile
      })
      setEditingId(id)
      setViewMode(isView)
      setIsFormOpen(true)
    }
  }

  const handleDelete = (id: string) => {
    const updated = additionalParties.filter(p => p.id !== id)
    updateDraft({ additionalParties: updated })
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (viewMode) {
      handleCloseForm()
      return
    }
    
    if (editingId) {
      const updated = additionalParties.map(p => 
        p.id === editingId ? { ...p, ...formData } : p
      )
      updateDraft({ additionalParties: updated })
    } else {
      const newParty = {
        id: crypto.randomUUID(),
        ...formData
      }
      updateDraft({ additionalParties: [...additionalParties, newParty] })
    }
    handleCloseForm()
  }

  return (
    <div className="bg-white rounded-lg border border-gray-200 shadow-sm p-8">
      {/* Header */}
      <div className="flex items-center gap-3 mb-8">
        <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">9</div>
        <h2 className="text-xl font-bold text-gray-800">Additional Parties</h2>
      </div>

      <div className="mb-4">
        <h3 className="text-sm font-bold text-gray-800 mb-3">Added Parties</h3>
        <div className="overflow-x-auto border border-gray-200 rounded-lg">
          <table className="w-full text-sm text-left">
            <thead className="text-xs text-gray-700 bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-4 py-3 font-semibold">NO</th>
                <th className="px-4 py-3 font-semibold">Name</th>
                <th className="px-4 py-3 font-semibold">Mobile Number</th>
                <th className="px-4 py-3 font-semibold">Party Type</th>
                <th className="px-4 py-3 font-semibold text-center">Action</th>
              </tr>
            </thead>
            <tbody>
              {additionalParties.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-4 py-8 text-center text-gray-500">
                    No additional parties added yet. Click "Add Another Party".
                  </td>
                </tr>
              ) : (
                additionalParties.map((party, index) => (
                  <tr key={party.id} className="border-b border-gray-100 hover:bg-gray-50">
                    <td className="px-4 py-3 text-gray-600">{index + 1}</td>
                    <td className="px-4 py-3 text-gray-800">{party.name}</td>
                    <td className="px-4 py-3 text-gray-800">{party.mobile}</td>
                    <td className="px-4 py-3 text-gray-600">{party.partyType}</td>
                    <td className="px-4 py-3 text-center flex justify-center gap-3">
                      <button 
                        type="button"
                        onClick={() => handleEdit(party.id, true)}
                        className="text-gray-500 hover:text-gray-700 transition-colors"
                        title="View"
                      >
                        <FiEye size={16} />
                      </button>
                      <button 
                        type="button"
                        onClick={() => handleEdit(party.id, false)}
                        className="text-blue-500 hover:text-blue-700 transition-colors"
                        title="Edit"
                      >
                        <FiEdit2 size={16} />
                      </button>
                      <button 
                        type="button"
                        onClick={() => handleDelete(party.id)}
                        className="text-red-500 hover:text-red-700 transition-colors"
                        title="Delete"
                      >
                        <FiTrash2 size={16} />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {!isFormOpen && (
        <div className="mt-4">
          <button 
            type="button"
            onClick={handleOpenForm}
            className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded text-sm font-medium hover:bg-blue-700 transition-colors"
          >
            <FiPlus size={16} /> Add Another Party
          </button>
        </div>
      )}

      {isFormOpen && (
        <form onSubmit={handleSubmit} className="mt-6 border border-gray-200 rounded-lg p-5 bg-gray-50">
          <h4 className="text-sm font-bold text-gray-800 mb-4">
            {viewMode ? 'View Party Details' : editingId ? 'Edit Party' : 'Add New Party'}
          </h4>
          
          <div className="flex flex-col gap-5">
            <div className="flex flex-col gap-1">
              <label className="text-xs font-semibold text-gray-700">Party Type <span className="text-red-500">*</span></label>
              <select 
                required
                disabled={viewMode}
                className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 disabled:bg-gray-100 disabled:text-gray-600"
                value={formData.partyType}
                onChange={e => setFormData({...formData, partyType: e.target.value})}
              >
                <option value="">Select Party Type</option>
                {PARTY_TYPES.map(type => (
                  <option key={type} value={type}>{type}</option>
                ))}
              </select>
            </div>

            <div className="flex flex-col gap-1">
              <label className="text-xs font-semibold text-gray-700">Full Name <span className="text-red-500">*</span></label>
              <input 
                type="text" 
                required
                disabled={viewMode}
                className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 disabled:bg-gray-100 disabled:text-gray-600" 
                placeholder="e.g. Mohd. Imran" 
                value={formData.name}
                onChange={e => setFormData({...formData, name: e.target.value})}
              />
            </div>

            <div className="flex flex-col gap-1">
              <label className="text-xs font-semibold text-gray-700">Address <span className="text-red-500">*</span></label>
              <input 
                type="text" 
                required
                disabled={viewMode}
                className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 disabled:bg-gray-100 disabled:text-gray-600" 
                placeholder="e.g. 789, Gomti Nagar, Lucknow" 
                value={formData.address}
                onChange={e => setFormData({...formData, address: e.target.value})}
              />
            </div>

            <div className="flex flex-col gap-1">
              <label className="text-xs font-semibold text-gray-700">Mobile <span className="text-red-500">*</span></label>
              <input 
                type="tel" 
                required
                pattern="\d{10}"
                maxLength={10}
                title="Mobile number must contain exactly 10 digits"
                disabled={viewMode}
                className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 w-full md:w-1/2 disabled:bg-gray-100 disabled:text-gray-600" 
                placeholder="10 digit mobile" 
                value={formData.mobile}
                onChange={e => setFormData({...formData, mobile: e.target.value})}
              />
            </div>
          </div>

          <div className="flex justify-end gap-3 mt-5">
            <button 
              type="button" 
              onClick={handleCloseForm}
              className="px-4 py-2 border border-gray-300 text-gray-700 rounded text-sm font-medium hover:bg-gray-100 transition-colors"
            >
              {viewMode ? 'Close' : 'Cancel'}
            </button>
            {!viewMode && (
              <button 
                type="submit"
                className="px-4 py-2 bg-blue-600 text-white rounded text-sm font-medium hover:bg-blue-700 transition-colors"
              >
                {editingId ? 'Save Changes' : 'Add Party'}
              </button>
            )}
          </div>
        </form>
      )}

      {/* Footer / Actions */}
      <div className="flex justify-between mt-10 border-t border-gray-100 pt-6">
        <button 
          type="button"
          onClick={onPrevious}
          className="px-8 py-2 border border-blue-600 text-blue-600 rounded font-medium hover:bg-blue-50 transition-colors shadow-sm"
        >
          Previous
        </button>
        <button 
          type="button"
          onClick={onNext}
          className="px-8 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors shadow-sm"
        >
          Next
        </button>
      </div>
    </div>
  )
}
