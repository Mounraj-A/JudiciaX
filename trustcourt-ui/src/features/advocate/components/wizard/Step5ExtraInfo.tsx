import React from 'react'

export function Step5ExtraInfo({ onNext, onPrevious }: { onNext: () => void, onPrevious: () => void }) {
  return (
    <div className="bg-white rounded-lg border border-gray-200 shadow-sm p-8">
      {/* Header */}
      <div className="flex items-center gap-3 mb-8">
        <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">5</div>
        <h2 className="text-xl font-bold text-gray-800">Extra Information</h2>
      </div>

      <div className="flex flex-col gap-5">
        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Passport Number</label>
          <input type="text" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 w-1/2" defaultValue="P1234567" />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Nationality</label>
          <input type="text" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 w-1/2" defaultValue="Indian" />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Occupation</label>
          <input type="text" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 w-1/2" defaultValue="Business" />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Email</label>
          <input type="email" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 w-1/2" defaultValue="ramesh@gmail.com" />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Mobile</label>
          <input type="text" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500 w-1/2" defaultValue="9876543210" />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Additional Address</label>
          <input type="text" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" defaultValue="Near Kaiserbagh, Lucknow, UP" />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Other Information</label>
          <input type="text" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" defaultValue="N/A" />
        </div>
      </div>

      {/* Footer / Actions */}
      <div className="flex justify-between mt-10">
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
