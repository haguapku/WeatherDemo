package com.example.weatherdemo.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherdemo.TestContextProvider
import com.example.weatherdemo.TestCoroutineRule
import com.example.weatherdemo.WeatherDaoFake
import com.example.weatherdemo.data.WeatherRepository
import com.example.weatherdemo.data.model.WeatherResponse
import com.example.weatherdemo.util.getOrWaitValue
import com.example.weatherdemo.util.readFileFromPath
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class WeatherViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var weatherViewModel: WeatherViewModel

    @Mock
    private lateinit var weatherRepository: WeatherRepository

    private lateinit var weatherResponse: WeatherResponse

//    @Mock
//    private lateinit var weatherResultObserver: Observer<WeatherResponse>

    private val latitude = -33.8F
    private val longitude = 151.0833F

    @Before
    fun setup() {
        weatherResponse = Gson().fromJson(readFileFromPath(), WeatherResponse::class.java)
        weatherViewModel = WeatherViewModel(
            weatherRepository,
            WeatherDaoFake(weatherResponse),
            TestContextProvider()
        ).apply {
//            weatherLivaData.observeForever(weatherResultObserver)
        }
    }

    @Test
    fun loadWeatherByCoordinates_returnSuccessfulData() {

        testCoroutineRule.runBlockingTest {

            // Given
            val response = Response.success(weatherResponse)
            `when`(weatherRepository.getWeatherByCoordinates(latitude, longitude)).thenReturn(response)

            // When
            weatherViewModel.getWeatherByCoordinates(latitude, longitude)

            // Then
            assertThat(weatherViewModel.weatherLivaData.getOrWaitValue {  }).isNotNull()
            assertThat(weatherViewModel.weatherLivaData.getOrWaitValue {  }).isEqualTo(weatherResponse)
//            verify(weatherResultObserver).onChanged(weatherResponse)

        }
    }
}