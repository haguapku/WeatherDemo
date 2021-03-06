package com.example.weatherdemo.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherdemo.viewmodel.SearchViewModelFactory
import com.example.weatherdemo.viewmodel.SearchViewModel
import com.example.weatherdemo.viewmodel.WeatherViewModel
import com.example.weatherdemo.viewmodel.WeatherViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(WeatherViewModel::class)
    internal abstract fun bindWeatherViewModel(weatherViewModel: WeatherViewModel): ViewModel

    @Binds
    internal abstract fun bindWeatherViewModelFactory(weatherViewModelFactory: WeatherViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    internal abstract fun bindSearchViewModel(searchViewModle: SearchViewModel): ViewModel

    @Binds
    internal abstract fun bindSearchViewModelFactory(searchViewModelFactory: SearchViewModelFactory): ViewModelProvider.Factory
}