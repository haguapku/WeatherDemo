package com.example.weatherdemo.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherdemo.util.WeatherListDataConverter

@Entity
data class WeatherInfo(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    @Embedded
    val temp: Temp,
    @Embedded
    val feels_like: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    @TypeConverters(WeatherListDataConverter::class)
    val weather: List<Weather>,
    val speed: Float,
    val deg: Int,
    val clouds: Int,
    val rain: Float)
{
    @PrimaryKey(autoGenerate = false)
    var weatherinfoid: Int = 0
}