import React from 'react'

export function Step10CourtFees({ onNext, onPrevious }: { onNext: () => void, onPrevious: () => void }) {
  return (
    <div className="bg-white rounded-lg border border-gray-200 shadow-sm p-8">
      {/* Header */}
      <div className="flex items-center gap-3 mb-8">
        <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">10</div>
        <h2 className="text-xl font-bold text-gray-800">Court Fees</h2>
      </div>

      <div className="flex flex-col gap-6">
        <div>
          <h3 className="text-sm font-bold text-gray-800 mb-4 border-b pb-2">Fee Details</h3>
          
          <div className="flex flex-col gap-3 text-sm">
            <div className="flex justify-between items-center text-gray-700">
              <span>Filing Fee</span>
              <span className="font-medium">₹ 500.00</span>
            </div>
            <div className="flex justify-between items-center text-gray-700">
              <span>Court Fee</span>
              <span className="font-medium">₹ 5,000.00</span>
            </div>
            <div className="flex justify-between items-center text-gray-700">
              <span>Processing Fee</span>
              <span className="font-medium">₹ 300.00</span>
            </div>
            <div className="flex justify-between items-center text-gray-700">
              <span>Service Charges</span>
              <span className="font-medium">₹ 50.00</span>
            </div>
            <div className="flex justify-between items-center text-gray-700">
              <span>GST (18%)</span>
              <span className="font-medium">₹ 1,053.00</span>
            </div>
            <div className="flex justify-between items-center mt-2 pt-3 border-t border-gray-200">
              <span className="font-bold text-gray-900 text-base">Total Amount</span>
              <span className="font-bold text-green-600 text-lg">₹ 6,903.00</span>
            </div>
          </div>
        </div>

        <div className="mt-4">
          <h3 className="text-sm font-bold text-gray-800 mb-4 border-b pb-2">Payment Method <span className="text-red-500">*</span></h3>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <label className="flex items-center gap-2 text-sm cursor-pointer">
              <input type="radio" name="paymentMethod" defaultChecked className="w-4 h-4 text-blue-600 focus:ring-blue-500" />
              UPI
            </label>
            <label className="flex items-center gap-2 text-sm cursor-pointer">
              <input type="radio" name="paymentMethod" className="w-4 h-4 text-blue-600 focus:ring-blue-500" />
              Net Banking
            </label>
            <label className="flex items-center gap-2 text-sm cursor-pointer">
              <input type="radio" name="paymentMethod" className="w-4 h-4 text-blue-600 focus:ring-blue-500" />
              Credit Card
            </label>
            <label className="flex items-center gap-2 text-sm cursor-pointer">
              <input type="radio" name="paymentMethod" className="w-4 h-4 text-blue-600 focus:ring-blue-500" />
              Debit Card
            </label>
            <label className="flex items-center gap-2 text-sm cursor-pointer col-span-2">
              <input type="radio" name="paymentMethod" className="w-4 h-4 text-blue-600 focus:ring-blue-500" />
              Offline (Challan)
            </label>
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
          Pay & Proceed
        </button>
      </div>
    </div>
  )
}
