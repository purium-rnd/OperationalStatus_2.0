package com.example.test

import android.Manifest
import android.app.Service
import android.location.LocationListener
import android.content.Intent
import android.os.IBinder
import android.os.Bundle
import android.location.LocationManager
import android.content.pm.PackageManager
import android.Manifest.permission
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.support.v4.content.ContextCompat
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.location.Location
import android.support.v4.content.ContextCompat.getSystemService
import android.util.Log


class GpsTracker : Service(),LocationListener{

    var mLocation: Location? = null
    var mLatitude: Double = 0.toDouble()
    var mLongitude: Double = 0.toDouble()

    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10
    private val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong()
    protected var locationManager: LocationManager? = null

    fun getLocation(context:Context): Location? {
        try {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {

                val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )


                if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
                } else
                    return null


                if (isNetworkEnabled) {


                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                        this
                    )

                    if (locationManager != null) {
                        mLocation = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (mLocation != null) {
                            mLatitude = mLocation!!.getLatitude()
                            mLongitude = mLocation!!.getLongitude()
                        }
                    }
                }


                if (isGPSEnabled) {
                    if (mLocation == null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                            this
                        )
                        if (locationManager != null) {
                            mLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (mLocation != null) {
                                mLatitude = mLocation!!.latitude
                                mLongitude = mLocation!!.longitude
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("@@@", "" + e.toString())
        }

        return mLocation
    }

    fun getLatitude(): Double {
        if (mLocation != null) {
            mLatitude = mLocation!!.latitude
        }

        return mLatitude
    }

    fun getLongitude(): Double {
        if (mLocation != null) {
            mLongitude = mLocation!!.longitude
        }

        return mLongitude
    }

    override fun onLocationChanged(location: Location) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }


    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@GpsTracker)
        }
    }
}