package com.example.weatherdemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherdemo.data.WeatherRepository
import com.example.weatherdemo.data.model.WeatherResponse
import com.example.weatherdemo.db.WeatherDao
import com.example.weatherdemo.util.CoroutineContextProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val weatherDao: WeatherDao,
    private val contextProvider: CoroutineContextProvider): ViewModel() {

    private val _snackBar = MutableLiveData<String>()

    val snackbar: LiveData<String>
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
                var weatherResponse = weatherRepository.getWeatherByCityName(name)
                if (weatherResponse.isSuccessful && weatherResponse.body()!!.cod.equals("200")) {
                    weatherDao.insertWeather(weatherResponse.body()!!)
                }
            }
        }
    }

    fun getWeatherByZipCode(name: String?) {

        return launchDataLoad {
            var weatherResponse = withContext(contextProvider.IO) {
                weatherRepository.getWeatherByZipCode(name)
            }
            if (weatherResponse.isSuccessful && weatherResponse.body()!!.cod.equals("200")) {
                withContext(contextProvider.IO) {
                    weatherDao.insertWeather(weatherResponse.body()!!)
                }
            }
        }
    }

    fun getWeatherByCoordinates(lat: Float?, lon: Float?) {

        return launchDataLoad {
            var weatherResponse = withContext(contextProvider.IO) {
                weatherRepository.getWeatherByCoordinates(lat, lon)
            }
            if (weatherResponse.isSuccessful && weatherResponse.body()!!.cod.equals("200")) {
                withContext(contextProvider.IO) {
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