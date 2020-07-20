package com.example.weatherdemo.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.weatherdemo.data.WeatherRepository
import com.example.weatherdemo.data.model.WeatherResponse
import com.example.weatherdemo.db.WeatherDao
import com.example.weatherdemo.util.CoroutineContextProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel @ViewModelInject constructor(
    private val weatherRepository: WeatherRepository,
    private val weatherDao: WeatherDao,
    private val contextProvider: CoroutineContextProvider): ViewModel() {

    private val _snackBar = MutableLiveData<String>()

    val snackBar: LiveData<String>
        get() = _snackBar

    val weatherLivaData: LiveData<WeatherResponse> by lazy(LazyThreadSafetyMode.NONE) {
        weatherDao.loadWeather()
    }

    private val handler = CoroutineExceptionHandler { _, throwable ->
        _snackBar.value = throwable.message
    }

    fun onSnackbarShown() {
        _snackBar.value = null
    }

    fun getWeatherByCityName(name: String?) {

        return launchDataLoad {
            withContext(contextProvider.IO) {
                val weatherResponse = weatherRepository.getWeatherByCityName(name)
                if (weatherResponse.isSuccessful && weatherResponse.body()!!.cod == "200") {
                    weatherDao.insertWeather(weatherResponse.body()!!)
                }
            }
        }
    }

    fun getWeatherByZipCode(name: String?) {

        return launchDataLoad {
            withContext(contextProvider.IO) {
                val weatherResponse = weatherRepository.getWeatherByZipCode(name)
                if (weatherResponse.isSuccessful && weatherResponse.body()!!.cod == "200") {
                    weatherDao.insertWeather(weatherResponse.body()!!)
                }
            }
        }
    }

    fun getWeatherByCoordinates(lat: Float?, lon: Float?) {

        return launchDataLoad {
            withContext(contextProvider.IO) {
                val weatherResponse = weatherRepository.getWeatherByCoordinates(lat, lon)
                if (weatherResponse.isSuccessful && weatherResponse.body()!!.cod == "200") {
                    weatherDao.insertWeather(weatherResponse.body()!!)
                }
            }
        }
    }

    private fun launchDataLoad(block:suspend () -> Unit) {
        viewModelScope.launch(handler) {
            try {
                block()
            } catch (error: WeatherRepository.WeatherRefreshError) {
                _snackBar.value = error.message
            }
        }
    }
}