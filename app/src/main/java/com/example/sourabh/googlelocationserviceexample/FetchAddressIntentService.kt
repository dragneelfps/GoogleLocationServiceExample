package com.example.sourabh.googlelocationserviceexample

import android.app.IntentService
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import java.io.IOException
import java.util.*

object FetchServiceConstants{
    const val SUCCESS_RESULT = 0
    const val FAILURE_RESULT = 1
    const val PACKAGE_NAME = "com.example.sourabh.googlelocationserviceexample"
    const val RECEIVER = "$PACKAGE_NAME.RECEIVER"
    const val RESULT_DATA_KEY = "${PACKAGE_NAME}.RESULT_DATA_KEY"
    const val LOCATION_DATA_EXTRA = "${PACKAGE_NAME}.LOCATION_DATA_EXTRA"

}

class FetchAddressIntentService : IntentService("FetchAddressIntentService") {

    private val mMaxResults = 1
    private val TAG = "FetchAddress"
    private lateinit var mResultReceiver: ResultReceiver

    override fun onHandleIntent(intent: Intent?) {
        intent ?: return
        var errorMessage = ""
        val mGeocoder = Geocoder(this, Locale.getDefault())
        val location = intent.getParcelableExtra<Location?>(FetchServiceConstants.LOCATION_DATA_EXTRA)
        location ?: return
        mResultReceiver = intent.getParcelableExtra<MainActivity.AddressResultReceiver>(FetchServiceConstants.RECEIVER)
        var addresses = emptyList<Address>()
        try{
            addresses = mGeocoder.getFromLocation(location.latitude, location.longitude, mMaxResults)
        }catch (ioException : IOException){
            errorMessage = "I/O Exception"
            Log.e(TAG, errorMessage, ioException)
        }catch (illegalArgumentException: IllegalArgumentException){
            errorMessage = "Illegal Arguments"
            Log.e(TAG, errorMessage, illegalArgumentException)
        }

        // Handle case where no address was found.
        if (addresses.isEmpty()) {
            if (errorMessage.isEmpty()) {
                errorMessage = "No address Found"
                Log.e(TAG, errorMessage)
            }
            deliverResultToReceiver(FetchServiceConstants.FAILURE_RESULT, errorMessage)
        } else {
            val address = addresses[0]
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            val addressFragments = with(address) {
                (0..maxAddressLineIndex).map { getAddressLine(it) }
            }
            Log.i(TAG, "Address Found")
            deliverResultToReceiver(FetchServiceConstants.SUCCESS_RESULT,
                    addressFragments.joinToString(separator = "\n"))
        }

    }

    private fun deliverResultToReceiver(resultCode: Int, address: String) {
        val bundle = Bundle().apply { putString(FetchServiceConstants.RESULT_DATA_KEY, address) }
        mResultReceiver.send(resultCode, bundle)

    }

}
