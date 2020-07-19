package com.example.weatherdemo.api

import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherdemo.WeatherApplication
import com.example.weatherdemo.util.readFileFromPath
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Rule
import org.robolectric.annotation.Config
import timber.log.Timber

@ExperimentalCoroutinesApi
@Config(sdk=[Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
class WeatherServiceTest {

    private var mockWebServer = MockWebServer()

    private lateinit var weatherService: WeatherService

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    @Before
    fun setup() {

        Dispatchers.setMain(testDispatcher)

        mockWebServer.start()

        weatherService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(WeatherService.createOkHttpClient(WeatherApplication.instance))
            .build().create(WeatherService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()

        Dispatchers.resetMain()
        testScope.cleanupTestCoroutines()
    }

    @Test
    fun getWeatherByCoordinatesTest_inputCorrectCoordinates() = runBlocking {

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(readFileFromPath())
        Timber.i(readFileFromPath())
        mockWebServer.enqueue(response)

        val weatherResponse = weatherService.getWeatherByCoordinates(-33.8F, 151.0833F)

        assertThat(weatherResponse.isSuccessful, `is`(true))
        assertThat(weatherResponse.body()?.city?.name, `is`("Eastwood"))
        }
}