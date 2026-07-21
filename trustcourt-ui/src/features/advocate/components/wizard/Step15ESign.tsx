import React, { useState } from 'react'

export function Step15ESign({ onNext, onPrevious }: { onNext: () => void, onPrevious: () => void }) {
  const [agreed, setAgreed] = useState(false)
  return (
    <div className="bg-white rounded-lg border border-gray-200 shadow-sm p-8">
      {/* Header */}
      <div className="flex items-center gap-3 mb-8">
        <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">15</div>
        <h2 className="text-xl font-bold text-gray-800">e-Sign</h2>
      </div>

      <div className="flex flex-col gap-6">
        <div>
          <h3 className="text-sm font-bold text-gray-800 mb-4 border-b pb-2">Choose Signing Method <span className="text-red-500">*</span></h3>
          <div className="flex gap-6 mt-2">
            <label className="flex items-center gap-2 text-sm cursor-pointer">
              <input type="radio" name="signMethod" defaultChecked className="w-4 h-4 text-blue-600 focus:ring-blue-500" />
              Aadhaar OTP
            </label>
            <label className="flex items-center gap-2 text-sm cursor-pointer">
              <input type="radio" name="signMethod" className="w-4 h-4 text-blue-600 focus:ring-blue-500" />
              Digital Signature (DSC)
            </label>
          </div>
        </div>

        <div className="mt-4">
          <p className="text-sm text-gray-600 mb-4">An OTP will be sent to your Aadhaar linked mobile number.</p>
          <div className="flex flex-col gap-1 w-1/2 md:w-1/3">
            <label className="text-xs font-semibold text-gray-700">Aadhaar Number <span className="text-red-500">*</span></label>
            <input type="text" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 text-center tracking-widest" defaultValue="XXXX XXXX 1234" />
          </div>
          <button className="mt-4 px-6 py-2 bg-blue-600 text-white rounded text-sm font-medium hover:bg-blue-700 transition-colors shadow-sm">
            Send OTP
          </button>
        </div>
      </div>

      <div className="mt-8 border-t border-gray-100 pt-6">
        <label className="flex items-center gap-3 cursor-pointer">
          <input 
            type="checkbox" 
            checked={agreed} 
            onChange={(e) => setAgreed(e.target.checked)}
            className="w-5 h-5 text-blue-600 rounded border-gray-300 focus:ring-blue-500" 
          />
          <span className="text-sm font-semibold text-gray-800">
            I agree to submit this case and confirm that all the provided information is true and accurate.
          </span>
        </label>
      </div>

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
          disabled={!agreed}
          className="px-8 py-2 bg-green-600 text-white rounded font-medium hover:bg-green-700 transition-colors shadow-sm disabled:opacity-50 disabled:cursor-not-allowed"
        >
          Submit
        </button>
      </div>
    </div>
  )
}
