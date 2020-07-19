package com.example.weatherdemo.api

import android.content.Context
import com.example.weatherdemo.util.NetWorkUtil
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

class BaseInterceptor(val context: Context): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()
        if (!NetWorkUtil.isNetWorkConnected(context)) {
            request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
        }
        return chain.proceed(request)
    }
}