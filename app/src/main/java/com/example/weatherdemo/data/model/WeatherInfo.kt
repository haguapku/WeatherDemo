package com.example.weatherdemo.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WeatherInfo(
    val dt: Long,
    @Embedded
    val main: MainData,
//    val weather: List<Weather>,
    @Embedded
    val clouds: Clouds,
    @Embedded
    val wind: Wind,
    @Embedded
    val sys: Sys,
    val dt_txt: String)
{
    @PrimaryKey(autoGenerate = false)
    var weatherinfoid: Int = 0
}