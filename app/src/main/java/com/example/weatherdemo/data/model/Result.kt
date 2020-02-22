package com.example.weatherdemo.data.model

sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val errorMessage: String?) : Result<Nothing>()
    object Loading : Result<Nothing>()
}