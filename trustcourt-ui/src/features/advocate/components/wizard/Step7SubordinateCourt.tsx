import React from 'react'

export function Step7SubordinateCourt({ onNext, onPrevious }: { onNext: () => void, onPrevious: () => void }) {
  return (
    <div className="bg-white rounded-lg border border-gray-200 shadow-sm p-8">
      {/* Header */}
      <div className="flex items-center gap-3 mb-8">
        <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">7</div>
        <h2 className="text-xl font-bold text-gray-800">Subordinate Court Details</h2>
      </div>

      <div className="flex flex-col gap-5">
        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Subordinate Court <span className="text-red-500">*</span></label>
          <select className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500">
            <option>Civil Judge (Senior Division), Lucknow</option>
          </select>
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Judge Name <span className="text-red-500">*</span></label>
          <input type="text" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" defaultValue="Shri A.K. Sharma" />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">CNR Number <span className="text-red-500">*</span></label>
          <input type="text" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" defaultValue="UPLC031000452024" />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs font-semibold text-gray-700">Case Number <span className="text-red-500">*</span></label>
          <input type="text" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" defaultValue="CS/345/2024" />
        </div>

        <div className="flex gap-8">
          <div className="flex flex-col gap-1 w-1/2">
            <label className="text-xs font-semibold text-gray-700">Filed</label>
            <input type="text" className="border border-gray-300 rounded px-3 py-2 text-sm bg-gray-50 text-gray-600 focus:outline-none" defaultValue="Yes" readOnly />
          </div>
          <div className="flex flex-col gap-1 w-1/2">
            <label className="text-xs font-semibold text-gray-700">Year</label>
            <input type="text" className="border border-gray-300 rounded px-3 py-2 text-sm bg-gray-50 text-gray-600 focus:outline-none" defaultValue="2024" readOnly />
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-5 mt-2">
          <div className="flex flex-col gap-1">
            <label className="text-xs font-semibold text-gray-700">Judgment Date (Impugned) <span className="text-red-500">*</span></label>
            <input type="date" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" defaultValue="2024-05-15" />
          </div>
          <div className="flex flex-col gap-1">
            <label className="text-xs font-semibold text-gray-700">Impugned Order Date <span className="text-red-500">*</span></label>
            <input type="date" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" defaultValue="2024-05-15" />
          </div>

          <div className="flex flex-col gap-1">
            <label className="text-xs font-semibold text-gray-700">CC Applied Date</label>
            <input type="date" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" defaultValue="2024-05-20" />
          </div>
          <div className="flex flex-col gap-1">
            <label className="text-xs font-semibold text-gray-700">CC Ready Date</label>
            <input type="date" className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500" defaultValue="2024-05-25" />
          </div>
        </div>
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
          className="px-8 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors shadow-sm"
        >
          Next
        </button>
      </div>
    </div>
  )
}
