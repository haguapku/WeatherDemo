package com.example.weatherdemo.di

import com.example.weatherdemo.WeatherApplication
import com.example.weatherdemo.api.WeatherService
import com.example.weatherdemo.db.WeatherDao
import com.example.weatherdemo.db.getDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun getWeatherService(): WeatherService = WeatherService.create()

    @Provides
    @Singleton
    fun getWeatherDao(): WeatherDao = getDatabase(WeatherApplication.instance).weatherDao
}