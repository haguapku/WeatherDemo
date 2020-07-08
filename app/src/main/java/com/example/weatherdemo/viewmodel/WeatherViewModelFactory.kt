package com.example.weatherdemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import javax.inject.Provider

@ActivityScoped
class WeatherViewModelFactory @Inject constructor(private val creators: Map<Class<out ViewModel>
        , @JvmSuppressWildcards Provider<ViewModel>>)
: ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val creator = creators[modelClass] ?:
        creators.asIterable().firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
        ?: throw IllegalArgumentException("unknown model class $modelClass")

        return try {
            creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}