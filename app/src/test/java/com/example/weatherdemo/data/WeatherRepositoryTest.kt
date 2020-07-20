package com.example.weatherdemo.data

import com.example.weatherdemo.api.WeatherService
import com.example.weatherdemo.data.model.WeatherResponse
import com.example.weatherdemo.util.readFileFromPath
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class WeatherRepositoryTest {

    private val weatherResponse = Gson().fromJson<WeatherResponse>(readFileFromPath(), WeatherResponse::class.java)
    private val mockResponse = Response.success(weatherResponse)
    private val latitude = -33.8F
    private val longitude = 151.0833F

    @Mock
    private lateinit var mockService: WeatherService

    @Test
    fun getWeatherByCoordinatesTest() = runBlockingTest {

        `when` (mockService.getWeatherByCoordinates(latitude, longitude)).thenReturn(mockResponse)
        val weatherRepository = WeatherRepository(mockService)

        val response = weatherRepository.getWeatherByCoordinates(latitude, longitude)

        assertThat(response.isSuccessful).isEqualTo(true)
        assertThat(response.body()?.city?.name).isEqualTo("Eastwood")
    }

}