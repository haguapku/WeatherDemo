package com.example.weatherdemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherdemo.data.model.City
import com.example.weatherdemo.data.model.Coord
import com.example.weatherdemo.data.model.WeatherInfo
import com.example.weatherdemo.data.model.WeatherResponse
import com.example.weatherdemo.db.WeatherDao
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class WeatherDaoFake(var weatherToReturn: WeatherResponse) : WeatherDao {

    /**
     * This is used to signal an element has been inserted.
     */
    private var nextInsertion: CompletableDeferred<WeatherResponse>? = null

    /**
     * Protect concurrent access to inserted and nextInsertion
     */
    private val mutex = Mutex()

    override fun insertWeather(weatherResponse: WeatherResponse) {
        runBlocking {
            mutex.withLock {
                // complete the waiting deferred
                nextInsertion?.complete(weatherResponse)
            }
        }
    }

    override fun loadWeather(): LiveData<WeatherResponse> {
        return MutableLiveData<WeatherResponse>().apply {
            value = weatherToReturn
        }
    }
}