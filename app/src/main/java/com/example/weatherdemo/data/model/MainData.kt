package com.example.weatherdemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MainData(
    val temp: Float,
    val feels_like: Float,
    val temp_min: Float,
    val temp_max: Float,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Float)
{
    @PrimaryKey(autoGenerate = false)
    var maindataid: Int = 0
}