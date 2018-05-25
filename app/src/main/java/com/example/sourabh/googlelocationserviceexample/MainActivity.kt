package com.example.sourabh.googlelocationserviceexample

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*

private val GOOGLE_SERVICE_DIALOG: Int = 1231
private val LOCATION_SETTINGS_REQ: Int = 34321

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private var mShouldRequestUpdates: Boolean = false

    /**
     * Toggles location update button and returns true if toggled to getting updates
     */
    fun toggleButton(): Boolean{
        mShouldRequestUpdates = !mShouldRequestUpdates
        toggleUpdateBtn.apply {
            text = when (mShouldRequestUpdates) {
                true -> {
                    "Stop Location Updates"
                }
                false -> {
                    "Start Location Updates"
                }
            }
        }
        return mShouldRequestUpdates
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GOOGLE_SERVICE_DIALOG -> {
                Toast.makeText(this, "Got result from google service check", Toast.LENGTH_SHORT).show()
                when (resultCode) {
                    Activity.RESULT_OK -> Toast.makeText(this, "Good Work", Toast.LENGTH_SHORT).show()
                    Activity.RESULT_CANCELED -> Toast.makeText(this, "Bad Work", Toast.LENGTH_SHORT).show()
                }
            }
            LOCATION_SETTINGS_REQ -> {
                Toast.makeText(this, "Got result from location settings change request", Toast.LENGTH_SHORT).show()
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Toast.makeText(this, "Location settings changed", Toast.LENGTH_SHORT).show()
                        initDefaultUI()
                    }
                    Activity.RESULT_CANCELED -> {
                        Toast.makeText(this, "Location settings weren't changed", Toast.LENGTH_SHORT).show()
                        initNullUI()
                    }
                }
            }
            LOCATION_PERMISSION_REQ -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        initDefaultUI()
                    }
                    Activity.RESULT_CANCELED -> {
                        initNullUI()
                    }
                }
            }
        }
    }

    private lateinit var mLocationRequest: LocationRequest

    private val LOCATION_PERMISSION_REQ: Int = 143

    override fun onResume() {
        super.onResume()
        if(hasPermssion(this)) {
            hasGooglePlayService2(this).let { result ->
                when (result) {
                    ConnectionResult.SUCCESS -> {
                        mLocationRequest = createLocationRequest(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        val settingsTask = createSettingsCheckTask(this, getLocationBuilder(mLocationRequest))
                        settingsTask.addOnSuccessListener { locationSettingsResponse ->
                            initDefaultUI()
                        }
                        settingsTask.addOnFailureListener { exception ->
                            if (exception is ResolvableApiException) {
                                exception.startResolutionForResult(this, LOCATION_SETTINGS_REQ)
                            }
                        }
                    }
                    else -> {
                        initNullUI()
                        val dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, result, GOOGLE_SERVICE_DIALOG)
                        dialog.show()
                    }
                }
            }
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ)
        }
    }

    private val toggleButtonListener: View.OnClickListener = View.OnClickListener { v ->
        if(toggleButton()){
            startLocationUpdates()
        }else{
            stopLocationUpdates()
        }
    }

    fun stopLocationUpdates(){
        fusedLocationProvider.removeLocationUpdates(mLocationCallback)
    }

    private val mLocationCallback: LocationCallback = object: LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.run {
                if(locations.isEmpty())
                    return
                for(location in locations){
                    updateUI(location)
                }
            }
        }
    }

    private fun updateUI(location: Location?) {
        location?.let {
            time.text = formatTime(it.time)
            latitude.text = it.latitude.toString()
            longitude.text = it.longitude.toString()
        }
    }


    fun startLocationUpdates(){
        fusedLocationProvider.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
    }

    private fun displayLastKnownLocation(){
        fusedLocationProvider.lastLocation.addOnSuccessListener { location ->
            if(location == null){
                initNullUI(enableButton = true)
            }else{
                updateUI(location)
            }
        }.addOnFailureListener { exception ->

        }
    }

    private lateinit var fusedLocationProvider: FusedLocationProviderClient

    private fun init(){
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun initDefaultUI(){
        val nl = ""
        time.text = nl
        latitude.text = nl
        longitude.text = nl
        toggleUpdateBtn.isEnabled = true
        displayLastKnownLocation()
        toggleUpdateBtn.setOnClickListener(toggleButtonListener)
    }

    private fun initNullUI(enableButton: Boolean = false){
        val nl = "null"
        time.text = nl
        latitude.text = nl
        longitude.text = nl
        toggleUpdateBtn.isEnabled = enableButton
        toggleUpdateBtn.setOnClickListener(null)
    }
}
