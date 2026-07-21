import { useState } from 'react'
import { FiTrash2, FiPlus, FiEdit2 } from 'react-icons/fi'
import { useDraft } from '../../pages/NewCaseWizardContext'


export function Step8ActSection({ onNext, onPrevious }: { onNext: () => void, onPrevious: () => void }) {
  const { draft, updateDraft } = useDraft()
  const caseActs = draft.caseActs || []
  
  const [isFormOpen, setIsFormOpen] = useState(false)
  const [editingId, setEditingId] = useState<string | null>(null)
  
  const [formData, setFormData] = useState({
    act: '',
    section: '',
    article: ''
  })

  const handleOpenForm = () => {
    setFormData({ act: '', section: '', article: '' })
    setEditingId(null)
    setIsFormOpen(true)
  }

  const handleCloseForm = () => {
    setIsFormOpen(false)
    setEditingId(null)
  }

  const handleEdit = (id: string) => {
    const actToEdit = caseActs.find(a => a.id === id)
    if (actToEdit) {
      setFormData({
        act: actToEdit.act,
        section: actToEdit.section,
        article: actToEdit.article
      })
      setEditingId(id)
      setIsFormOpen(true)
    }
  }

  const handleDelete = (id: string) => {
    const updatedActs = caseActs.filter(a => a.id !== id)
    updateDraft({ caseActs: updatedActs })
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (editingId) {
      const updatedActs = caseActs.map(a => 
        a.id === editingId ? { ...a, ...formData } : a
      )
      updateDraft({ caseActs: updatedActs })
    } else {
      const newAct = {
        id: crypto.randomUUID(),
        ...formData
      }
      updateDraft({ caseActs: [...caseActs, newAct] })
    }
    handleCloseForm()
  }

  return (
    <div className="bg-white rounded-lg border border-gray-200 shadow-sm p-8">
      {/* Header */}
      <div className="flex items-center gap-3 mb-8">
        <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">8</div>
        <h2 className="text-xl font-bold text-gray-800">Act & Section</h2>
      </div>

      {/* Acts Table */}
      <div className="mb-4">
        <h3 className="text-sm font-bold text-gray-800 mb-3">Acts & Sections</h3>
        <div className="overflow-x-auto border border-gray-200 rounded-lg">
          <table className="w-full text-sm text-left">
            <thead className="text-xs text-gray-700 bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-4 py-3 font-semibold">S.No.</th>
                <th className="px-4 py-3 font-semibold">Act</th>
                <th className="px-4 py-3 font-semibold">Section</th>
                <th className="px-4 py-3 font-semibold">Article</th>
                <th className="px-4 py-3 font-semibold text-center">Action</th>
              </tr>
            </thead>
            <tbody>
              {caseActs.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-gray-500">
                    No acts added yet. Click "Add More" to add an act.
                  </td>
                </tr>
              ) : (
                caseActs.map((item, index) => (
                  <tr key={item.id} className="border-b border-gray-100 hover:bg-gray-50">
                    <td className="px-4 py-3 text-gray-600">{index + 1}</td>
                    <td className="px-4 py-3 text-gray-800">{item.act}</td>
                    <td className="px-4 py-3 text-gray-800">{item.section}</td>
                    <td className="px-4 py-3 text-gray-600">{item.article || '-'}</td>
                    <td className="px-4 py-3 text-center flex justify-center gap-3">
                      <button 
                        type="button"
                        onClick={() => handleEdit(item.id)}
                        className="text-blue-500 hover:text-blue-700 transition-colors"
                        title="Edit"
                      >
                        <FiEdit2 size={16} />
                      </button>
                      <button 
                        type="button"
                        onClick={() => handleDelete(item.id)}
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
        <button 
          type="button"
          onClick={handleOpenForm}
          className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded text-sm font-medium hover:bg-blue-700 transition-colors"
        >
          <FiPlus size={16} /> Add More
        </button>
      )}

      {isFormOpen && (
        <form onSubmit={handleSubmit} className="mt-6 border border-gray-200 rounded-lg p-5 bg-gray-50">
          <h4 className="text-sm font-bold text-gray-800 mb-4">{editingId ? 'Edit Act' : 'Add New Act'}</h4>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="flex flex-col gap-1">
              <label className="text-xs font-semibold text-gray-700">Act <span className="text-red-500">*</span></label>
              <input 
                type="text" 
                required 
                className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" 
                placeholder="e.g. The Indian Contract Act, 1872" 
                value={formData.act} 
                onChange={e => setFormData({...formData, act: e.target.value})} 
              />
            </div>
            <div className="flex flex-col gap-1">
              <label className="text-xs font-semibold text-gray-700">Section <span className="text-red-500">*</span></label>
              <input 
                type="text" 
                required
                pattern="\d{1,5}"
                maxLength={5}
                title="Section must contain only digits and be up to 5 digits long"
                className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" 
                placeholder="e.g. 73" 
                value={formData.section} 
                onChange={e => setFormData({...formData, section: e.target.value.replace(/\D/g, '')})} 
              />
            </div>
            <div className="flex flex-col gap-1">
              <label className="text-xs font-semibold text-gray-700">Article</label>
              <input 
                type="text" 
                className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" 
                placeholder="Optional" 
                value={formData.article} 
                onChange={e => setFormData({...formData, article: e.target.value})} 
              />
            </div>
          </div>
          <div className="flex justify-end gap-3 mt-5">
            <button 
              type="button" 
              onClick={handleCloseForm}
              className="px-4 py-2 border border-gray-300 text-gray-700 rounded text-sm font-medium hover:bg-gray-100 transition-colors"
            >
              Cancel
            </button>
            <button 
              type="submit"
              className="px-4 py-2 bg-blue-600 text-white rounded text-sm font-medium hover:bg-blue-700 transition-colors"
            >
              {editingId ? 'Save Changes' : 'Add Act'}
            </button>
          </div>
        </form>
      )}

      {/* Footer / Actions */}
      <div className="flex justify-between mt-10 border-t border-gray-100 pt-6">
        <button 
          onClick={onPrevious}
          className="px-8 py-2 border border-blue-600 text-blue-600 rounded font-medium hover:bg-blue-50 transition-colors shadow-sm"
        >
          Previous
        </button>
        <button 
          onClick={onNext}
          className="px-8 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors shadow-sm"
        >
          Next
        </button>
      </div>
    </div>
  )
}
