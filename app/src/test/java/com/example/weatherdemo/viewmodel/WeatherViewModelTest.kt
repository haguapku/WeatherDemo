package com.example.weatherdemo.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.weatherdemo.TestContextProvider
import com.example.weatherdemo.TestCoroutineRule
import com.example.weatherdemo.WeatherDaoFake
import com.example.weatherdemo.api.WeatherService
import com.example.weatherdemo.data.WeatherRepository
import com.example.weatherdemo.data.model.City
import com.example.weatherdemo.data.model.Coord
import com.example.weatherdemo.data.model.WeatherInfo
import com.example.weatherdemo.data.model.WeatherResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class WeatherViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var weatherViewModel: WeatherViewModel

    private lateinit var weatherRepository: WeatherRepository

    private lateinit var weatherResponse: WeatherResponse

    @Mock
    private lateinit var weatherResultObserver: Observer<WeatherResponse>

    @Mock
    private lateinit var weatherService: WeatherService

    @Before
    fun setup() {
        weatherRepository = WeatherRepository(weatherService)
        weatherResponse =  WeatherResponse(
            "200",
            "0",
            0,
            emptyList<WeatherInfo>(),
            City(
                2167280,
                "Epping",
                Coord(
                    1.1,
                    1.1),
                "AU",
                18969,
                39600,
                1582400260,
                1582447261))
        weatherViewModel = WeatherViewModel(
            weatherRepository,
            WeatherDaoFake(weatherResponse),
            TestContextProvider()
        ).apply {
            weatherLivaData.observeForever(weatherResultObserver)
        }
    }

    @Test
    fun loadWeatherByName_fetchFromServer_returnProperData() {

        testCoroutineRule.runBlockingTest {

            // Given
            val response = Response.success(weatherResponse)
            Mockito.`when`(weatherService.getWeatherByCityName("Epping")).thenReturn(response)
            val weatherInfoResponse =  WeatherResponse(
                "200",
                "0",
                0,
                emptyList<WeatherInfo>(),
                City(
                    2167280,
                    "Epping",
                    Coord(
                        1.1,
                        1.1),
                    "AU",
                    18969,
                    39600,
                    1582400260,
                    1582447261))
            // When
            weatherViewModel.getWeatherByCityName("Epping")

            // Then
            Mockito.verify(weatherResultObserver).onChanged(weatherInfoResponse)

        }
    }
}