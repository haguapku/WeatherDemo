package com.example.weatherdemo.data

import com.example.weatherdemo.api.WeatherService
import com.example.weatherdemo.data.model.WeatherResponse
import dagger.hilt.android.scopes.ActivityScoped
import retrofit2.Response
import javax.inject.Inject

@ActivityScoped
class WeatherRepository @Inject constructor(
    private val weatherService: WeatherService) {

    suspend fun getWeatherByCityName(name: String?): Response<WeatherResponse> {

        val result = weatherService.getWeatherByCityName(name)
        if (result.isSuccessful){
            return result
        }
        else {
            throw WeatherRefreshError(Throwable(result.message()))
        }
    }

    suspend fun getWeatherByZipCode(zipcode: String?): Response<WeatherResponse> {

        val result = weatherService.getWeatherByZipCode(zipcode)
        if (result.isSuccessful){
            return result
        }
        else {
            throw WeatherRefreshError(Throwable(result.message()))
        }
    }

    suspend fun getWeatherByCoordinates(lat: Float?, lon: Float?): Response<WeatherResponse> {

        val result = weatherService.getWeatherByCoordinates(lat, lon)
        if (result.isSuccessful){
            return result
        }
        else {
            throw WeatherRefreshError(Throwable(result.message()))
        }
    }

    class WeatherRefreshError(cause: Throwable) : Throwable(cause.message, cause)
}