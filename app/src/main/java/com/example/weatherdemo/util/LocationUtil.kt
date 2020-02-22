package com.example.weatherdemo.util

import android.content.Context
import android.preference.PreferenceManager
import android.R
import android.location.Location


class LocationUtil {

    val KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates"

    fun requestingLocationUpdates(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false)
    }

    fun setRequestingLocationUpdates(context: Context, requestingLocationUpdates: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
            .apply()
    }

    fun getLocationText(location: Location?): String {
        return if (location == null)
            "Unknown location"
        else
            "(" + location!!.getLatitude() + ", " + location!!.getLongitude() + ")"
    }
}