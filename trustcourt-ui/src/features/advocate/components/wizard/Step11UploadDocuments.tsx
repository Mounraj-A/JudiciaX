import React, { useState } from 'react'
import { FiTrash2, FiDownload, FiEye } from 'react-icons/fi'
import { useDraft } from '../../pages/NewCaseWizardContext'
import { casesService } from '@/api/services/cases/cases.service'

const DOCUMENT_TYPES = [
  "Petition / Plaint", "Written Statement", "Affidavit", "Vakalatnama", 
  "Memo of Parties", "List of Documents", "Court Fee Receipt", "Identity Proof", 
  "Address Proof", "Aadhaar Card", "PAN Card", "Passport", "Driving Licence", 
  "Birth Certificate", "Marriage Certificate", "Death Certificate", "FIR Copy", 
  "Charge Sheet", "Police Report", "Medical Report", "Disability Certificate", 
  "Forensic Report", "Expert Opinion", "Witness Statement", "Agreement", 
  "Sale Deed", "Lease Deed", "Gift Deed", "Mortgage Deed", "Property Tax Receipt", 
  "Encumbrance Certificate", "Patta / Chitta", "Survey Sketch", "Photographs", 
  "Audio Recording", "Video Recording", "Email Evidence", "WhatsApp Chat", 
  "Call Recording", "SMS Screenshot", "Bank Statement", "Income Proof", 
  "Salary Slip", "Electricity Bill", "Water Bill", "Previous Court Order", 
  "Previous Judgment", "Certified Copy", "Appeal Memo", "Annexure", "Exhibit", 
  "Miscellaneous Document"
]

export function Step11UploadDocuments({ onNext, onPrevious }: { onNext: () => void, onPrevious: () => void }) {
  const { draft, updateDraft } = useDraft()
  const uploadedDocuments = draft.uploadedDocuments || []

  const [documentType, setDocumentType] = useState('')
  const [documentTitle, setDocumentTitle] = useState('')
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
    if (!documentType || !documentTitle || !file) return
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
      formData.append('documentType', documentType)
      formData.append('description', documentTitle)

      await casesService.uploadDocument(formData)

      // Assuming successful upload, we update the draft UI
      const fileUrl = URL.createObjectURL(file)
      const newDoc = {
        id: crypto.randomUUID(),
        documentType,
        documentTitle,
        fileName: file.name,
        fileUrl
      }
      updateDraft({ uploadedDocuments: [...uploadedDocuments, newDoc] })

      setDocumentType('')
      setDocumentTitle('')
      setFile(null)
      const fileInput = document.getElementById('docUpload') as HTMLInputElement
      if (fileInput) fileInput.value = ''
    } catch (err: any) {
      setErrorMsg(err.response?.data?.message || 'Failed to upload document')
    } finally {
      setIsUploading(false)
    }
  }

  const handleDelete = (id: string, fileUrl?: string) => {
    if (fileUrl) {
      URL.revokeObjectURL(fileUrl)
    }
    const updated = uploadedDocuments.filter(d => d.id !== id)
    updateDraft({ uploadedDocuments: updated })
  }

  const handleNext = () => {
    // Only go next if they've explicitly pressed the Next button
    onNext()
  }

  return (
    <div className="bg-white rounded-lg border border-gray-200 shadow-sm p-8">
      {/* Header */}
      <div className="flex items-center gap-3 mb-8">
        <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">11</div>
        <h2 className="text-xl font-bold text-gray-800">Upload Documents</h2>
      </div>

      {errorMsg && (
        <div className="mb-6 p-4 bg-red-50 text-red-700 border border-red-200 rounded-lg">
          {errorMsg}
        </div>
      )}

      <div className="mb-8">
        <h3 className="text-sm font-bold text-gray-800 mb-3">Uploaded Documents</h3>
        <div className="overflow-x-auto border border-gray-200 rounded-lg">
          <table className="w-full text-sm text-left">
            <thead className="text-xs text-gray-700 bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-4 py-3 font-semibold">Document Type</th>
                <th className="px-4 py-3 font-semibold">Document Title</th>
                <th className="px-4 py-3 font-semibold">File Name</th>
                <th className="px-4 py-3 font-semibold text-center">Action</th>
              </tr>
            </thead>
            <tbody>
              {uploadedDocuments.length === 0 ? (
                <tr>
                  <td colSpan={4} className="px-4 py-8 text-center text-gray-500">
                    No documents uploaded yet.
                  </td>
                </tr>
              ) : (
                uploadedDocuments.map((doc) => (
                  <tr key={doc.id} className="border-b border-gray-100 hover:bg-gray-50">
                    <td className="px-4 py-3 text-gray-800">{doc.documentType}</td>
                    <td className="px-4 py-3 text-gray-600">{doc.documentTitle}</td>
                    <td className="px-4 py-3 text-gray-600">{doc.fileName}</td>
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
                        <a 
                          href={doc.fileUrl || '#'} 
                          download={doc.fileName}
                          className="text-gray-500 hover:text-gray-700 transition-colors" 
                          title="Download"
                        >
                          <FiDownload size={16}/>
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

      <form onSubmit={handleUpload} className="mt-8 border border-gray-200 rounded-lg p-5">
        <h3 className="text-sm font-bold text-gray-800 mb-4 border-b pb-2">Upload New Document</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-5 mb-4">
          <div className="flex flex-col gap-1">
            <label className="text-xs font-semibold text-gray-700">Document Type <span className="text-red-500">*</span></label>
            <select 
              required
              className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500"
              value={documentType}
              onChange={e => setDocumentType(e.target.value)}
            >
              <option value="">Select Document Type</option>
              {DOCUMENT_TYPES.map(type => (
                <option key={type} value={type}>{type}</option>
              ))}
            </select>
          </div>
          <div className="flex flex-col gap-1">
            <label className="text-xs font-semibold text-gray-700">Document Title <span className="text-red-500">*</span></label>
            <input 
              type="text" 
              required
              className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" 
              placeholder="e.g. Main Petition"
              value={documentTitle}
              onChange={e => setDocumentTitle(e.target.value)}
            />
          </div>
        </div>
        <div className="flex flex-col gap-1 w-full md:w-1/2">
          <label className="text-xs font-semibold text-gray-700">Browse PDF/DOC/DOCX File <span className="text-red-500">*</span></label>
          <div className="flex gap-2">
            <div className="flex-1 flex items-center border border-gray-300 rounded px-3 py-2 text-sm text-gray-500 bg-gray-50">
              <input 
                type="file" 
                required
                accept=".pdf,.doc,.docx,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                className="hidden" 
                id="docUpload" 
                onChange={handleFileChange}
              />
              <label htmlFor="docUpload" className="cursor-pointer font-medium text-gray-700 bg-gray-200 px-3 py-1 rounded mr-3 text-xs border border-gray-300 hover:bg-gray-300 transition-colors">Choose File</label>
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
