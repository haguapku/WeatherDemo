package com.example.weatherdemo.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherdemo.data.model.WeatherResponse
import com.example.weatherdemo.util.getOrWaitValueInstrumented
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MainDatabaseTest {

    private lateinit var weatherDao: WeatherDao
    private lateinit var db: WeatherDatabase

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

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
        val responseAsLiveData = weatherDao.loadWeather()
        weatherDao.insertWeather(weatherResponse)
        val response = responseAsLiveData.getOrWaitValueInstrumented {  }
        assertThat(response).isEqualTo(weatherResponse)
    }
}