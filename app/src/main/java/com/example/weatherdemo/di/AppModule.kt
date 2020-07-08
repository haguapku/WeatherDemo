package com.example.weatherdemo.di

import com.example.weatherdemo.WeatherApplication
import com.example.weatherdemo.api.WeatherService
import com.example.weatherdemo.db.SearchDao
import com.example.weatherdemo.db.WeatherDao
import com.example.weatherdemo.db.getDatabase
import com.example.weatherdemo.ui.WeeklyWeatherAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
class AppModule {

    @Provides
    @ActivityScoped
    fun getWeatherService(): WeatherService = WeatherService.create()

    @Provides
    @ActivityScoped
    fun getWeatherDao(): WeatherDao = getDatabase(WeatherApplication.instance).weatherDao

    @Provides
    @ActivityScoped
    fun getSearchDao(): SearchDao = getDatabase(WeatherApplication.instance).searchDao

    @Provides
    @ActivityScoped
    fun getWeeklyWeatherAdapter(): WeeklyWeatherAdapter = WeeklyWeatherAdapter(ArrayList())

}