import React from 'react'

export function Step6AdvocateDetails({ onNext, onPrevious }: { onNext: () => void, onPrevious: () => void }) {
  return (
    <div className="bg-white rounded-lg border border-gray-200 shadow-sm p-8">
      {/* Header */}
      <div className="flex items-center gap-3 mb-8">
        <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">6</div>
        <h2 className="text-xl font-bold text-gray-800">Advocate Details <span className="text-sm font-normal text-gray-500">(Auto-filled)</span></h2>
      </div>

      <div className="flex flex-col md:flex-row gap-8">
        {/* Profile Picture Placeholder */}
        <div className="w-32 h-40 bg-gray-100 border border-gray-300 flex-shrink-0 flex items-center justify-center text-gray-400">
          Photo
        </div>

        {/* Details Grid */}
        <div className="flex-grow grid grid-cols-1 md:grid-cols-2 gap-y-4 text-sm">
          <div className="font-semibold text-gray-600">Advocate Name</div>
          <div className="text-gray-900 font-bold">Adv. Rajesh Kumar</div>

          <div className="font-semibold text-gray-600">Enrollment Number</div>
          <div className="text-gray-900">UP/12345/2012</div>

          <div className="font-semibold text-gray-600">Bar Council</div>
          <div className="text-gray-900">Uttar Pradesh State Bar Council</div>

          <div className="font-semibold text-gray-600">Enrollment State</div>
          <div className="text-gray-900">Uttar Pradesh</div>

          <div className="font-semibold text-gray-600">Enrollment Date</div>
          <div className="text-gray-900">12-08-2012</div>

          <div className="font-semibold text-gray-600">Office Address</div>
          <div className="text-gray-900">Chamber No. 12, District Court,<br/>Civil Lines, Lucknow, UP - 226001</div>

          <div className="font-semibold text-gray-600">Mobile</div>
          <div className="text-gray-900">9876543210</div>

          <div className="font-semibold text-gray-600">Email</div>
          <div className="text-gray-900">rajesh.kumar@law.com</div>

          <div className="font-semibold text-gray-600">GST (Optional)</div>
          <div className="text-gray-900">09AABCD1234F1Z5</div>
        </div>
      </div>

      <div className="mt-8 text-center text-red-500 font-semibold text-sm">
        (Read Only - Cannot Edit)
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
