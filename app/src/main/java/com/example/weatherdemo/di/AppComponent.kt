package com.example.weatherdemo.di

import android.app.Application
import com.example.weatherdemo.WeatherApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    BuildersModule::class,
    ViewModelModule::class])
interface AppComponent: AndroidInjector<WeatherApplication> {

    @Component.Builder
    interface Bulider{
        @BindsInstance
        fun application(application: Application): Bulider
        fun build(): AppComponent
    }
}