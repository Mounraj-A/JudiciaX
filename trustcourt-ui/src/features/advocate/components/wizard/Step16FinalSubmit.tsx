import React from 'react'
import { FiFileText, FiCheck } from 'react-icons/fi'

export function Step16FinalSubmit({ onNext, onPrevious }: { onNext: () => void, onPrevious: () => void }) {
  return (
    <div className="bg-white rounded-lg border border-gray-200 shadow-sm p-8 flex flex-col items-center">
      {/* Header */}
      <div className="flex items-center gap-3 mb-8 self-start">
        <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">16</div>
        <h2 className="text-xl font-bold text-gray-800">Final Submit</h2>
      </div>

      <div className="flex flex-col items-center max-w-md w-full mt-10 mb-16 text-center">
        <div className="relative mb-6">
          <FiFileText size={80} className="text-gray-300" />
          <div className="absolute -bottom-2 -right-2 bg-green-500 rounded-full p-1 text-white border-4 border-white">
            <FiCheck size={24} />
          </div>
        </div>
        
        <p className="text-gray-700 font-medium mb-2">You are about to finally submit your case.</p>
        <p className="text-sm text-gray-500 mb-8">Once submitted, the case will be forwarded to the Court Registry for scrutiny.</p>

        <label className="flex items-start gap-3 text-sm text-gray-700 bg-gray-50 p-4 rounded border border-gray-200 w-full cursor-pointer text-left">
          <input type="checkbox" defaultChecked className="mt-1 w-4 h-4 text-blue-600 rounded focus:ring-blue-500 flex-shrink-0" />
          <span>I confirm that all the information provided is true and correct.</span>
        </label>
      </div>

      {/* Footer / Actions */}
      <div className="flex justify-between w-full border-t border-gray-100 pt-6">
        <button 
          onClick={onPrevious}
          className="px-8 py-2 border border-blue-600 text-blue-600 rounded font-medium hover:bg-blue-50 transition-colors shadow-sm"
        >
          Previous
        </button>
        <button 
          onClick={onNext}
          className="px-8 py-2 bg-green-600 text-white rounded font-medium hover:bg-green-700 transition-colors shadow-sm"
        >
          Final Submit
        </button>
      </div>
    </div>
  )
}
