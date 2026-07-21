import React, { useMemo } from 'react'
import { FiCheckCircle } from 'react-icons/fi'
import { useDraft } from '../../pages/NewCaseWizardContext'
import { useNavigate } from 'react-router-dom'

export function Step17Success() {
  const { draft, resetDraft } = useDraft()
  const navigate = useNavigate()

  // Generate dynamic acknowledgement number once when component mounts
  const ackNo = useMemo(() => {
    const year = new Date().getFullYear()
    const random = Math.floor(100000 + Math.random() * 900000)
    return `ACK/${year}/${random}`
  }, [])

  // Generate real-time filing date once when component mounts
  const filingDate = useMemo(() => {
    return new Date().toLocaleString('en-IN', {
      day: '2-digit', month: '2-digit', year: 'numeric',
      hour: '2-digit', minute: '2-digit', hour12: true
    })
  }, [])

  const caseTitle = draft.caseTitle || draft.causeTitle || 'Untitled Case'

  const handleDownload = () => {
    window.print()
  }

  const handleGoHome = () => {
    resetDraft()
    navigate('/advocate/dashboard')
  }

  return (
    <div id="printable-receipt" className="bg-white rounded-lg border border-gray-200 shadow-sm p-8 flex flex-col items-center print:border-none print:shadow-none print:p-0">
      {/* Header - hide on print */}
      <div className="flex items-center gap-3 mb-8 self-start print:hidden">
        <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">15</div>
        <h2 className="text-xl font-bold text-gray-800">Success</h2>
      </div>

      <div className="flex flex-col items-center max-w-2xl w-full mt-10 mb-12 print:mt-8 print:mb-0 print:max-w-sm print:mx-auto">
        <FiCheckCircle size={80} className="text-green-500 mb-6" />
        
        <h3 className="text-2xl font-bold text-gray-800 mb-8">Case Filed Successfully!</h3>
        
        {/* Receipt Details Area */}
        <div className="w-full grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-6 text-sm bg-gray-50 p-6 rounded-lg border border-gray-100 mb-10 print:!grid-cols-1 print:border-none print:bg-white print:p-0 print:gap-y-6 print:text-left">
          
          <div className="flex flex-col gap-1">
            <span className="text-gray-500 font-semibold">Acknowledgement No.</span>
            <span className="text-gray-900 font-bold">{ackNo}</span>
          </div>
          <div className="flex flex-col gap-1">
            <span className="text-gray-500 font-semibold">Filing Date</span>
            <span className="text-gray-900 font-bold">{filingDate}</span>
          </div>
          
          <div className="flex flex-col gap-1">
            <span className="text-gray-500 font-semibold">Case Title</span>
            <span className="text-gray-900 font-bold">{caseTitle}</span>
          </div>
          <div className="flex flex-col gap-1">
            <span className="text-gray-500 font-semibold">Payment Status</span>
            <span className="text-gray-900 font-bold">Paid (₹ 6,903.00)</span>
          </div>
          
          <div className="flex flex-col gap-1">
            <span className="text-gray-500 font-semibold">Status</span>
            <span className="text-green-600 font-bold">Submitted</span>
          </div>
        </div>

        {/* Action Buttons - hide on print */}
        <div className="flex flex-wrap justify-center gap-4 print:hidden">
          <button 
            onClick={handleDownload}
            className="px-6 py-2 bg-green-600 text-white rounded font-medium hover:bg-green-700 transition-colors shadow-sm"
          >
            Download Acknowledgement
          </button>
          <button 
            onClick={() => navigate('/advocate/my-cases')}
            className="px-6 py-2 border border-blue-600 text-blue-600 rounded font-medium hover:bg-blue-50 transition-colors shadow-sm"
          >
            View My Cases
          </button>
          <button 
            onClick={handleGoHome}
            className="px-6 py-2 text-blue-600 font-medium hover:underline"
          >
            Go to Home
          </button>
        </div>
      </div>
    </div>
  )
}
