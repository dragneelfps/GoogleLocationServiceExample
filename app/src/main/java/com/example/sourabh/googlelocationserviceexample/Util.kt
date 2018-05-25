package com.example.sourabh.googlelocationserviceexample

import android.location.Location
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

fun hasGooglePlayService(context: AppCompatActivity): Boolean{
    val instance = GoogleApiAvailability.getInstance()
    return when (instance.isGooglePlayServicesAvailable(context)){
        ConnectionResult.SUCCESS -> true
        else -> {
            val dialog = instance.getErrorDialog(context, instance.isGooglePlayServicesAvailable(context), 456)
            dialog.show()
            false
        }
    }
}

fun parseSpeed(location: Location): String {
    var speed = location.speed.toString()
    if(hasSdk(26)) {
        if (location.hasSpeedAccuracy()) {
            speed = "$speed / ${location.speedAccuracyMetersPerSecond}"
        }
    }
    return speed
}

fun parseAltitude(location: Location): String? {
    var alt = location.altitude.toString()
    if(hasSdk(26)) {
        if (location.hasVerticalAccuracy()) {
            alt = "$alt / ${location.verticalAccuracyMeters}"
        }
    }
    return alt
}

fun hasSdk(requiredSdkVersion: Int): Boolean {
    return android.os.Build.VERSION.SDK_INT >= requiredSdkVersion
}