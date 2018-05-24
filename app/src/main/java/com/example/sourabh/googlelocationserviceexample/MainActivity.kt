package com.example.sourabh.googlelocationserviceexample

import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProvider: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(hasGooglePlayService()) {
            init()
            if (hasSdk(21)) {
                if (hasPermssion()) {
                    getLastKnownLocation()
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 123)
                }
            } else {
                getLastKnownLocation()
            }
        }
    }

    fun hasGooglePlayService(): Boolean{
        val instance = GoogleApiAvailability.getInstance()
        return when (instance.isGooglePlayServicesAvailable(this)){
            ConnectionResult.SUCCESS -> true
            else -> {
                val dialog = instance.getErrorDialog(this, instance.isGooglePlayServicesAvailable(this), 456)
                dialog.show()
                false
            }
        }
    }

    fun init(){
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
    }

    fun hasPermssion(): Boolean{
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 123 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getLastKnownLocation()
        }
    }

    private fun getLastKnownLocation() {
        fusedLocationProvider.lastLocation.addOnSuccessListener { location ->
            location?.let{
                latitude.text = it.latitude.toString()
                longitude.text = it.longitude.toString()
                if(it.hasAltitude()){
                    altitude.text = parseAltitude(it)
                }else{
                    altitude.text = "Unavailable"
                }
                if(it.hasSpeed()){
                    speed.text = parseSpeed(it)
                }else{
                    speed.text = "Unavailable"
                }
                if(hasSdk(18)){
                    if(it.isFromMockProvider){
                        isMocked.text = "Yes"
                    }else{
                        isMocked.text = "No"
                    }
                }else{
                    isMocked.text = "Unvailable"
                }
                time.text = it.time.toString()
            }
        }
    }
    private fun parseSpeed(location: Location): String {
        var speed = location.speed.toString()
        if(hasSdk(26)) {
            if (location.hasSpeedAccuracy()) {
                speed = "$speed / ${location.speedAccuracyMetersPerSecond}"
            }
        }
        return speed
    }

    private fun parseAltitude(location: Location): String? {
        var alt = location.altitude.toString()
        if(hasSdk(26)) {
            if (location.hasVerticalAccuracy()) {
                alt = "$alt / ${location.verticalAccuracyMeters}"
            }
        }
        return alt
    }


    private fun hasSdk(requiredSdkVersion: Int): Boolean {
        return android.os.Build.VERSION.SDK_INT >= requiredSdkVersion
    }
}
