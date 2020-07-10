package com.example.weatherdemo.api

import com.example.weatherdemo.BuildConfig
import com.example.weatherdemo.WeatherApplication
import com.example.weatherdemo.data.model.WeatherResponse
import io.reactivex.Single
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.File
import java.util.concurrent.TimeUnit

const val BASE_URL = "http://api.openweathermap.org"

interface WeatherService {

    @GET("http://api.openweathermap.org/data/2.5/forecast/daily?cnt=8&units=metric&APPID=" + BuildConfig.API_KEY)
    suspend fun getWeatherByCityName(@Query("q") q: String?): Response<WeatherResponse>

    @GET("http://api.openweathermap.org/data/2.5/forecast/daily?cnt=8&units=metric&APPID=" + BuildConfig.API_KEY)
    suspend fun getWeatherByZipCode(@Query("zip") zip: String?): Response<WeatherResponse>

    @GET("http://api.openweathermap.org/data/2.5/forecast/daily?cnt=8&units=metric&APPID=" + BuildConfig.API_KEY)
    suspend fun getWeatherByCoordinates(@Query("lat") lat: Float?, @Query("lon") lon: Float?): Response<WeatherResponse>

    @GET("http://api.openweathermap.org/data/2.5/forecast/daily?cnt=8&units=metric&APPID=" + BuildConfig.API_KEY)
    fun getWeatherByCityNameRx(@Query("q") q: String?) :Single<WeatherResponse>

    companion object {
        fun create(): WeatherService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(createOkHttpClient())
            .build().create(WeatherService::class.java)

        private fun createOkHttpClient(): OkHttpClient {
            val cache = Cache(File(WeatherApplication.instance.cacheDir, "httpCache"), (1024 * 1024 * 100).toLong())
            return OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(BaseInterceptor())
                .addNetworkInterceptor(HttpCacheInterceptor())
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build()
        }
    }

}