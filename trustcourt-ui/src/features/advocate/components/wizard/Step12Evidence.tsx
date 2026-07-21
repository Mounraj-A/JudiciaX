import React, { useState } from 'react'
import { FiTrash2, FiEye } from 'react-icons/fi'
import { useDraft } from '../../pages/NewCaseWizardContext'
import { casesService } from '@/api/services/cases/cases.service'

export function Step12Evidence({ onNext, onPrevious }: { onNext: () => void, onPrevious: () => void }) {
  const { draft, updateDraft } = useDraft()
  const evidenceFiles = draft.evidenceFiles || []

  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [file, setFile] = useState<File | null>(null)
  const [isUploading, setIsUploading] = useState(false)
  const [errorMsg, setErrorMsg] = useState('')
  
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setFile(e.target.files[0])
    }
  }

  const handleUpload = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!title || !description || !file) return
    if (!draft.caseUuid) {
      setErrorMsg('No Case UUID found. Please go back to Step 1 and try again.')
      return
    }

    setIsUploading(true)
    setErrorMsg('')
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('caseUuid', draft.caseUuid)
      formData.append('title', title)
      formData.append('description', description)

      await casesService.uploadEvidence(formData)

      // Assuming successful upload, update UI
      const fileUrl = URL.createObjectURL(file)
      const newEvidence = {
        id: crypto.randomUUID(),
        title,
        description,
        fileName: file.name,
        fileUrl
      }
      updateDraft({ evidenceFiles: [...evidenceFiles, newEvidence] })

      setTitle('')
      setDescription('')
      setFile(null)
      const fileInput = document.getElementById('evidenceUpload') as HTMLInputElement
      if (fileInput) fileInput.value = ''
    } catch (err: any) {
      setErrorMsg(err.response?.data?.message || 'Failed to upload evidence')
    } finally {
      setIsUploading(false)
    }
  }

  const handleDelete = (id: string, fileUrl?: string) => {
    if (fileUrl) {
      URL.revokeObjectURL(fileUrl)
    }
    const updated = evidenceFiles.filter(d => d.id !== id)
    updateDraft({ evidenceFiles: updated })
  }

  const handleNext = () => {
    onNext()
  }

  return (
    <div className="bg-white rounded-lg border border-gray-200 shadow-sm p-8">
      {/* Header */}
      <div className="flex items-center gap-3 mb-8">
        <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">12</div>
        <h2 className="text-xl font-bold text-gray-800">Evidence / Exhibits</h2>
      </div>

      {errorMsg && (
        <div className="mb-6 p-4 bg-red-50 text-red-700 border border-red-200 rounded-lg">
          {errorMsg}
        </div>
      )}

      <div className="mb-8">
        <h3 className="text-sm font-bold text-gray-800 mb-3">Uploaded Evidence</h3>
        <div className="overflow-x-auto border border-gray-200 rounded-lg">
          <table className="w-full text-sm text-left">
            <thead className="text-xs text-gray-700 bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-4 py-3 font-semibold">NO</th>
                <th className="px-4 py-3 font-semibold">Title</th>
                <th className="px-4 py-3 font-semibold">File Name</th>
                <th className="px-4 py-3 font-semibold">Description</th>
                <th className="px-4 py-3 font-semibold text-center">Action</th>
              </tr>
            </thead>
            <tbody>
              {evidenceFiles.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-4 py-8 text-center text-gray-500">
                    No evidence uploaded yet.
                  </td>
                </tr>
              ) : (
                evidenceFiles.map((doc, index) => (
                  <tr key={doc.id} className="border-b border-gray-100 hover:bg-gray-50">
                    <td className="px-4 py-3 text-gray-600">{index + 1}</td>
                    <td className="px-4 py-3 text-gray-800">{doc.title}</td>
                    <td className="px-4 py-3 text-gray-600">{doc.fileName}</td>
                    <td className="px-4 py-3 text-gray-600 max-w-xs whitespace-normal break-words">{doc.description}</td>
                    <td className="px-4 py-3 text-center">
                      <div className="flex justify-center items-center gap-3">
                        <a 
                          href={doc.fileUrl || '#'} 
                          target="_blank" 
                          rel="noreferrer"
                          className="text-blue-600 hover:text-blue-800 transition-colors" 
                          title="View"
                        >
                          <FiEye size={16}/>
                        </a>
                        <button 
                          type="button"
                          onClick={() => handleDelete(doc.id, doc.fileUrl)}
                          className="text-red-500 hover:text-red-700 transition-colors" 
                          title="Delete"
                        >
                          <FiTrash2 size={16}/>
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      <form onSubmit={handleUpload} className="mt-8 border border-gray-200 rounded-lg p-5 bg-gray-50">
        <h3 className="text-sm font-bold text-gray-800 mb-4 border-b pb-2">Upload New Evidence</h3>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-5 mb-4">
          <div className="flex flex-col gap-1">
            <label className="text-xs font-semibold text-gray-700">Evidence Title <span className="text-red-500">*</span></label>
            <input 
              type="text" 
              required
              className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" 
              placeholder="e.g. CCTV Footage"
              value={title}
              onChange={e => setTitle(e.target.value)}
            />
          </div>
          <div className="flex flex-col gap-1">
            <label className="text-xs font-semibold text-gray-700">Description <span className="text-red-500">*</span></label>
            <input 
              type="text" 
              required
              maxLength={100}
              className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" 
              placeholder="Brief description (max 100 chars)"
              value={description}
              onChange={e => setDescription(e.target.value)}
            />
            <span className="text-xs text-gray-500 text-right">{description.length}/100</span>
          </div>
        </div>

        <div className="flex flex-col gap-1 w-full md:w-1/2">
          <label className="text-xs font-semibold text-gray-700">Browse File <span className="text-red-500">*</span></label>
          <div className="flex gap-2">
            <div className="flex-1 flex items-center border border-gray-300 rounded px-3 py-2 text-sm text-gray-500 bg-white">
              <input 
                type="file" 
                required
                accept=".pdf,.jpg,.jpeg,.png,.tiff,.mp4,.mov,.avi,.mkv,.mp3,.wav,.aac"
                className="hidden" 
                id="evidenceUpload" 
                onChange={handleFileChange}
              />
              <label htmlFor="evidenceUpload" className="cursor-pointer font-medium text-gray-700 bg-gray-200 px-3 py-1 rounded mr-3 text-xs border border-gray-300 hover:bg-gray-300 transition-colors">Choose File</label>
              <span className="truncate">{file ? file.name : 'No file chosen'}</span>
            </div>
            <button 
              type="submit"
              disabled={isUploading}
              className="px-6 py-2 bg-blue-600 text-white rounded text-sm font-medium hover:bg-blue-700 transition-colors shadow-sm disabled:bg-gray-400"
            >
              {isUploading ? 'Uploading...' : 'Upload'}
            </button>
          </div>
        </div>
      </form>

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
          onClick={handleNext}
          className="px-8 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors shadow-sm"
        >
          Next
        </button>
      </div>
    </div>
  )
}
