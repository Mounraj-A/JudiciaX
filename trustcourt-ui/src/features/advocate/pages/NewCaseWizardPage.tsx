import React, { useState } from 'react'
import { PageHeader } from '@/shared/components/layout'
import { Input, Select } from '@/shared/components/form'
import { useDraft, DraftProvider } from './NewCaseWizardContext'
import { useNavigate } from 'react-router-dom'
import { ROUTES } from '@/constants/routes'
import { casesService } from '@/api/services/cases/cases.service'
import { FiCheckCircle, FiChevronRight, FiChevronLeft } from 'react-icons/fi'
import { Step1WhereToFile } from '../components/wizard/Step1WhereToFile'
import { Step2CaseInfo } from '../components/wizard/Step2CaseInfo'
import { Step3PetitionerDetails } from '../components/wizard/Step3PetitionerDetails'
import { Step4RespondentDetails } from '../components/wizard/Step4RespondentDetails'
import { Step5ExtraInfo } from '../components/wizard/Step5ExtraInfo'
import { Step6AdvocateDetails } from '../components/wizard/Step6AdvocateDetails'
import { Step7SubordinateCourt } from '../components/wizard/Step7SubordinateCourt'
import { Step8ActSection } from '../components/wizard/Step8ActSection'
import { Step9AdditionalParties } from '../components/wizard/Step9AdditionalParties'
import { Step10CourtFees } from '../components/wizard/Step10CourtFees'
import { Step11UploadDocuments } from '../components/wizard/Step11UploadDocuments'
import { Step12Evidence } from '../components/wizard/Step12Evidence'
import { Step14Review } from '../components/wizard/Step14Review'
import { Step15ESign } from '../components/wizard/Step15ESign'
import { Step17Success } from '../components/wizard/Step17Success'

const STEPS = [
  'Where to File', 'Case Info', 'Petitioner', 'Respondent', 
  'Extra Info', 'Advocate', 'Subordinate Court', 'Act & Section', 
  'Extra Party', 'Court Fees', 'Upload Docs', 'Evidence', 
  'Review UI', 'File + eSign', 'Success'
]

function WizardContent() {
  const { currentStep, setCurrentStep, draft, updateDraft } = useDraft()
  const navigate = useNavigate()
  const [errorMsg, setErrorMsg] = useState('')

  const handleNext = async () => {
    setErrorMsg('')
    try {
      if (currentStep < STEPS.length - 1) {
        if (currentStep === 13) {
          // Submit the case on the E-Sign step (index 13)
          if (!draft.caseUuid) throw new Error('No Case UUID found to submit.')
          await casesService.submit(draft.caseUuid)
        } else {
          // Continuous sync to backend on every step transition
          const res = await casesService.upsertDraft(draft)
          
          // Ensure caseUuid is captured if it was newly created
          if (res.data?.data?.caseUuid && !draft.caseUuid) {
            updateDraft({ caseUuid: res.data.data.caseUuid })
          }
        }
        
        setCurrentStep(currentStep + 1)
      }
    } catch (err: any) {
      setErrorMsg(err.response?.data?.message || err.message || 'Failed to save draft.')
    }
  }

  const handlePrevious = () => {
    if (currentStep > 0) {
      setCurrentStep(currentStep - 1)
    }
  }

  const renderStepContent = () => {
    switch (currentStep) {
      case 0: return <Step1WhereToFile onNext={handleNext} />
      case 1: return <Step2CaseInfo onNext={handleNext} onPrevious={handlePrevious} />
      case 2: return <Step3PetitionerDetails onNext={handleNext} onPrevious={handlePrevious} />
      case 3: return <Step4RespondentDetails onNext={handleNext} onPrevious={handlePrevious} />
      case 4: return <Step5ExtraInfo onNext={handleNext} onPrevious={handlePrevious} />
      case 5: return <Step6AdvocateDetails onNext={handleNext} onPrevious={handlePrevious} />
      case 6: return <Step7SubordinateCourt onNext={handleNext} onPrevious={handlePrevious} />
      case 7: return <Step8ActSection onNext={handleNext} onPrevious={handlePrevious} />
      case 8: return <Step9AdditionalParties onNext={handleNext} onPrevious={handlePrevious} />
      case 9: return <Step10CourtFees onNext={handleNext} onPrevious={handlePrevious} />
      case 10: return <Step11UploadDocuments onNext={handleNext} onPrevious={handlePrevious} />
      case 11: return <Step12Evidence onNext={handleNext} onPrevious={handlePrevious} />
      case 12: return <Step14Review onNext={handleNext} onPrevious={handlePrevious} />
      case 13: return <Step15ESign onNext={handleNext} onPrevious={handlePrevious} />
      case 14: return <Step17Success />
      default: 
        return (
          <div className="p-8 text-center text-gray-500 bg-white rounded-lg border border-gray-200">
            <h3>{STEPS[currentStep]} Details</h3>
            <p className="mb-6">This section is currently under construction.</p>
            <div className="flex justify-center gap-4">
              <button 
                onClick={handlePrevious}
                className="px-6 py-2 border border-blue-600 text-blue-600 rounded font-medium hover:bg-blue-50 transition-colors"
              >
                Previous
              </button>
              <button 
                onClick={handleNext}
                className="px-6 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors"
              >
                Next
              </button>
            </div>
          </div>
        )
    }
  }

  return (
    <div className="max-w-[1400px] mx-auto pb-12">
      {errorMsg && (
        <div className="mb-6 p-4 bg-red-50 text-red-700 border border-red-200 rounded-lg flex items-center gap-3">
          <div className="w-2 h-2 rounded-full bg-red-500 animate-pulse" />
          {errorMsg}
        </div>
      )}

      {/* Progress Tracker based on image design */}
      <div className="mb-10 w-full overflow-x-auto pb-4">
        <div className="flex items-center min-w-max px-4">
          {STEPS.map((step, index) => {
            const isActive = index === currentStep
            const isPast = index < currentStep
            return (
              <React.Fragment key={step}>
                <div className="flex flex-col items-center relative z-10 w-24">
                  <div className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold transition-colors
                    ${isPast ? 'bg-blue-600 text-white' : isActive ? 'bg-blue-600 text-white shadow-md' : 'bg-white border-2 border-gray-300 text-gray-500'}`}
                  >
                    {isPast ? <FiCheckCircle size={16} /> : index + 1}
                  </div>
                  <div className={`mt-2 text-xs font-semibold text-center ${isActive ? 'text-blue-600' : 'text-gray-500'}`}>
                    {step}
                  </div>
                </div>
                {index < STEPS.length - 1 && (
                  <div className={`h-[2px] w-12 -mx-4 -mt-6 z-0 ${isPast ? 'bg-blue-600' : 'bg-gray-300'}`} />
                )}
              </React.Fragment>
            )
          })}
        </div>
      </div>

      {/* Step Content Area */}
      <div>
        {renderStepContent()}
      </div>
    </div>
  )
}

export function NewCaseWizardPage() {
  return (
    <div className="min-h-screen bg-[#F8FAFC]">
      <PageHeader 
        title="Case Filing"
        description="From Start to End with All Details (Based on eFiling User Manual)"
      />
      <div className="p-6">
        <DraftProvider>
          <WizardContent />
        </DraftProvider>
      </div>
    </div>
  )
}