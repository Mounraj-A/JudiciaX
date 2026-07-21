import React, { createContext, useContext, useState, useEffect } from 'react'

export interface CaseDraft {
  caseUuid?: string
  
  // Step 1: Where to File
  state?: string
  district?: string
  court?: string
  courtType?: string
  bench?: string
  courtHall?: string
  filingMode?: string
  caseType?: string

  // Step 2: Case Information
  caseTitle?: string
  causeTitle?: string
  subject?: string
  caseCategoryUuid?: string
  natureOfSuit?: string
  reliefSought?: string
  shortDescription?: string
  detailedDescription?: string
  causeOfAction?: string
  dateOfCauseOfAction?: string
  
  // Step 3: Petitioners (Can be array in the future, for now single)
  petitionerName?: string
  petitionerAlias?: string
  petitionerGender?: string
  petitionerDob?: string
  petitionerAge?: number
  petitionerOccupation?: string
  petitionerAadhaar?: string
  petitionerPan?: string
  petitionerMobile?: string
  petitionerEmail?: string
  petitionerAddress?: string
  petitionerState?: string
  petitionerDistrict?: string
  petitionerPincode?: string

  // Step 4: Respondents
  respondentName?: string
  respondentAlias?: string
  respondentGender?: string
  respondentDob?: string
  respondentAge?: number
  respondentOccupation?: string
  respondentAadhaar?: string
  respondentPan?: string
  respondentMobile?: string
  respondentEmail?: string
  respondentAddress?: string
  respondentState?: string
  respondentDistrict?: string
  respondentPincode?: string
  
  // Step 5: Legal Info
  policeStation?: string
  firNumber?: string
  acts?: string
  sections?: string

  // Step 8: Act & Section
  caseActs?: {
    id: string
    act: string
    section: string
    article: string
  }[]

  // Step 9: Additional Parties
  additionalParties?: {
    id: string
    partyType: string
    name: string
    address: string
    mobile: string
  }[]

  // Step 11: Upload Documents
  uploadedDocuments?: {
    id: string
    documentType: string
    documentTitle: string
    fileName: string
    fileUrl?: string
  }[]

  // Step 12: Evidence
  evidenceFiles?: {
    id: string
    title: string
    description: string
    fileName: string
    fileUrl?: string
  }[]
}

interface DraftContextType {
  draft: CaseDraft
  updateDraft: (updates: Partial<CaseDraft>) => void
  currentStep: number
  setCurrentStep: (step: number) => void
  resetDraft: () => void
}

const DraftContext = createContext<DraftContextType | undefined>(undefined)

export function DraftProvider({ children }: { children: React.ReactNode }) {
  const [draft, setDraft] = useState<CaseDraft>(() => {
    const saved = localStorage.getItem('caseDraft')
    return saved ? JSON.parse(saved) : {}
  })
  
  const [currentStep, setCurrentStep] = useState(0)

  useEffect(() => {
    localStorage.setItem('caseDraft', JSON.stringify(draft))
  }, [draft])

  const updateDraft = (updates: Partial<CaseDraft>) => {
    setDraft(prev => ({ ...prev, ...updates }))
  }
  
  const resetDraft = () => {
    setDraft({})
    setCurrentStep(0)
    localStorage.removeItem('caseDraft')
  }

  return (
    <DraftContext.Provider value={{ draft, updateDraft, currentStep, setCurrentStep, resetDraft }}>
      {children}
    </DraftContext.Provider>
  )
}

export const useDraft = () => {
  const ctx = useContext(DraftContext)
  if (!ctx) throw new Error('useDraft must be used within DraftProvider')
  return ctx
}
