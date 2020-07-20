package com.example.weatherdemo.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.weatherdemo.data.model.WeatherResponse
import com.example.weatherdemo.util.getOrWaitValueInstrumented
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
class MainDatabaseTest {

    private lateinit var weatherDao: WeatherDao
    private lateinit var db: WeatherDatabase

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var observer: Observer<WeatherResponse>

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java).build()
        weatherDao = db.weatherDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun insertWeatherAndReadWeatherTest() {

        val weatherResponse = Gson().fromJson<WeatherResponse>(this::class.java.getResource("/test.json")?.readText(), WeatherResponse::class.java)
//        val responseAsLiveData = weatherDao.loadWeather()
//        weatherDao.insertWeather(weatherResponse)
//        val response = responseAsLiveData.getOrWaitValueInstrumented {  }
//        assertThat(response).isEqualTo(weatherResponse)

        weatherDao.loadWeather().apply { this.observeForever(observer) }
        weatherDao.insertWeather(weatherResponse)
        verify(observer).onChanged(weatherResponse)
    }
}