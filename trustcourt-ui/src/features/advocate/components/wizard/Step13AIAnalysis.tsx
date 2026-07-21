import React from 'react'

export function Step13AIAnalysis({ onNext, onPrevious }: { onNext: () => void, onPrevious: () => void }) {
  return (
    <div className="bg-white rounded-lg border border-gray-200 shadow-sm p-8">
      {/* Header */}
      <div className="flex items-center gap-3 mb-8">
        <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">13</div>
        <h2 className="text-xl font-bold text-gray-800">AI Analysis</h2>
      </div>

      <div className="flex flex-col gap-6">
        <div>
          <h3 className="text-sm font-bold text-gray-800 mb-4 border-b pb-2">AI & OCR Analysis</h3>
          
          <div className="flex flex-col gap-4 text-sm">
            <div className="flex justify-between items-center text-gray-700">
              <span className="font-semibold w-1/2">OCR Status</span>
              <span className="text-green-600 font-medium w-1/2 text-right">Completed</span>
            </div>
            <div className="flex justify-between items-center text-gray-700">
              <span className="font-semibold w-1/2">Extracted Text</span>
              <span className="text-green-600 font-medium w-1/2 text-right">Available (98%)</span>
            </div>
            <div className="flex justify-between items-center text-gray-700">
              <span className="font-semibold w-1/2">Duplicate Detection</span>
              <span className="text-green-600 font-medium w-1/2 text-right">No Duplicates Found</span>
            </div>
            <div className="flex justify-between items-center text-gray-700">
              <span className="font-semibold w-1/2">Missing Documents</span>
              <span className="text-green-600 font-medium w-1/2 text-right">None</span>
            </div>
            <div className="flex justify-between items-center text-gray-700">
              <span className="font-semibold w-1/2">Missing Fields</span>
              <span className="text-green-600 font-medium w-1/2 text-right">None</span>
            </div>
            <div className="flex justify-between items-center text-gray-700">
              <span className="font-semibold w-1/2">Suggested Acts</span>
              <span className="text-gray-900 font-medium w-1/2 text-right">Indian Contract Act, 1872</span>
            </div>
            <div className="flex justify-between items-center text-gray-700">
              <span className="font-semibold w-1/2">Suggested Sections</span>
              <span className="text-green-600 font-medium w-1/2 text-right">Section 73, 74</span>
            </div>
            <div className="flex justify-between items-center text-gray-700">
              <span className="font-semibold w-1/2">Similar Cases</span>
              <span className="text-green-600 font-medium w-1/2 text-right cursor-pointer hover:underline">12 Cases Found</span>
            </div>
            <div className="flex justify-between items-center text-gray-700">
              <span className="font-semibold w-1/2">AI Confidence</span>
              <span className="text-green-600 font-medium w-1/2 text-right">92%</span>
            </div>
            <div className="flex justify-between items-center text-gray-700">
              <span className="font-semibold w-1/2">Risk Score</span>
              <span className="text-green-600 font-medium w-1/2 text-right">Low Risk</span>
            </div>
            <div className="flex justify-between items-center text-gray-700">
              <span className="font-semibold w-1/2">Trust Score</span>
              <span className="text-green-600 font-medium w-1/2 text-right">88/100</span>
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
          Next
        </button>
      </div>
    </div>
  )
}
