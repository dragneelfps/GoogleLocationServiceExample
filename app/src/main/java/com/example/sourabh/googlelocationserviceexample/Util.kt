package com.example.sourabh.googlelocationserviceexample

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task
import java.text.SimpleDateFormat
import java.util.*

/**
 * Creates and returns a location request object with specified priority and interval rate
 */
fun createLocationRequest(priorityParam: Int = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, intervalParam: Long = 5000): LocationRequest{
    return  LocationRequest().apply {
        interval = intervalParam
        fastestInterval = intervalParam
        priority = priorityParam
    }
}

fun getLocationBuilder(locationRequest: LocationRequest): LocationSettingsRequest.Builder {
    return LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
}

fun createSettingsCheckTask(context: Context, builder: LocationSettingsRequest.Builder) : Task<LocationSettingsResponse> {
    val client = LocationServices.getSettingsClient(context)
    return client.checkLocationSettings(builder.build())
}

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
fun hasGooglePlayService2(context: AppCompatActivity): Int{
    val instance = GoogleApiAvailability.getInstance()
    return instance.isGooglePlayServicesAvailable(context)
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

fun hasPermssion(context: Context): Boolean{
    return ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
}

fun formatTime(time: Long): String{
    val date = Date(time)
    val df = SimpleDateFormat("dd/MM/yy hh:mm:ss", Locale.getDefault())
    return df.format(date)
}