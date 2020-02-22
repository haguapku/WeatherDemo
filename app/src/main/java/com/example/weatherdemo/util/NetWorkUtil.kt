package com.example.weatherdemo.util

import android.content.Context
import android.net.ConnectivityManager
import com.example.weatherdemo.WeatherApplication

class NetWorkUtil {
    companion object {
        fun isNetWorkConnected(): Boolean {
            val cm = WeatherApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val currentNet = cm.activeNetworkInfo ?: return false
            return currentNet.isAvailable
        }
    }
}