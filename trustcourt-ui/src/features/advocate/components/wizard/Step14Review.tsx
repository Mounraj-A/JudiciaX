import React from 'react'

export function Step14Review({ onNext, onPrevious }: { onNext: () => void, onPrevious: () => void }) {
  return (
    <div className="bg-white rounded-lg border border-gray-200 shadow-sm p-8">
      {/* Header */}
      <div className="flex items-center gap-3 mb-8">
        <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">14</div>
        <h2 className="text-xl font-bold text-gray-800">Review</h2>
      </div>

      <div className="flex flex-col gap-6">
        <div>
          <h3 className="text-sm font-bold text-gray-800 mb-4 border-b pb-2">Review Your Case Before Submission</h3>
          
          <div className="flex flex-col gap-3 text-sm">
            <div className="flex justify-between items-center text-gray-700 border-b border-gray-100 pb-2">
              <span className="font-semibold w-1/3">Court & Bench</span>
              <span className="text-blue-600 font-medium w-1/3 text-right cursor-pointer hover:underline">Edit</span>
            </div>
            <div className="flex justify-between items-center text-gray-700 border-b border-gray-100 pb-2">
              <span className="font-semibold w-1/3">Case Information</span>
              <span className="text-blue-600 font-medium w-1/3 text-right cursor-pointer hover:underline">Edit</span>
            </div>
            <div className="flex justify-between items-center text-gray-700 border-b border-gray-100 pb-2">
              <span className="font-semibold w-1/3">Petitioner Details</span>
              <span className="text-blue-600 font-medium w-1/3 text-right cursor-pointer hover:underline">Edit</span>
            </div>
            <div className="flex justify-between items-center text-gray-700 border-b border-gray-100 pb-2">
              <span className="font-semibold w-1/3">Respondent Details</span>
              <span className="text-blue-600 font-medium w-1/3 text-right cursor-pointer hover:underline">Edit</span>
            </div>
            <div className="flex justify-between items-center text-gray-700 border-b border-gray-100 pb-2">
              <span className="font-semibold w-1/3">Advocate Details</span>
              <span className="text-blue-600 font-medium w-1/3 text-right cursor-pointer hover:underline">View</span>
            </div>
            <div className="flex justify-between items-center text-gray-700 border-b border-gray-100 pb-2">
              <span className="font-semibold w-1/3">Subordinate Court Details</span>
              <span className="text-blue-600 font-medium w-1/3 text-right cursor-pointer hover:underline">Edit</span>
            </div>
            <div className="flex justify-between items-center text-gray-700 border-b border-gray-100 pb-2">
              <span className="font-semibold w-1/3">Act & Sections</span>
              <span className="text-blue-600 font-medium w-1/3 text-right cursor-pointer hover:underline">Edit</span>
            </div>
            <div className="flex justify-between items-center text-gray-700 border-b border-gray-100 pb-2">
              <span className="font-semibold w-1/3">Additional Parties</span>
              <span className="text-blue-600 font-medium w-1/3 text-right cursor-pointer hover:underline">Edit</span>
            </div>
            <div className="flex justify-between items-center text-gray-700 border-b border-gray-100 pb-2">
              <span className="font-semibold w-1/3">Court Fees</span>
              <span className="text-gray-900 font-medium w-1/3 text-center">₹ 6,903.00</span>
              <span className="text-blue-600 font-medium w-1/3 text-right cursor-pointer hover:underline">Edit</span>
            </div>
            <div className="flex justify-between items-center text-gray-700 border-b border-gray-100 pb-2">
              <span className="font-semibold w-1/3">Documents</span>
              <span className="text-gray-900 font-medium w-1/3 text-center">4 Uploaded</span>
              <span className="text-blue-600 font-medium w-1/3 text-right cursor-pointer hover:underline">Edit</span>
            </div>
            <div className="flex justify-between items-center text-gray-700 border-b border-gray-100 pb-2">
              <span className="font-semibold w-1/3">Evidence</span>
              <span className="text-gray-900 font-medium w-1/3 text-center">4 Uploaded</span>
              <span className="text-blue-600 font-medium w-1/3 text-right cursor-pointer hover:underline">Edit</span>
            </div>
            <div className="flex justify-between items-center text-gray-700 pb-2">
              <span className="font-semibold w-1/3">AI Validation</span>
              <span className="text-green-600 font-medium w-1/3 text-center">Passed</span>
              <span className="text-blue-600 font-medium w-1/3 text-right cursor-pointer hover:underline">View</span>
            </div>
          </div>
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
          Proceed to e-Sign
        </button>
      </div>
    </div>
  )
}
