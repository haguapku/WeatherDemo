package com.example.weatherdemo.data.model

import androidx.room.*
import com.example.weatherdemo.util.ListDataConverter

@Entity(tableName = "weather_table")
data class WeatherResponse(
    val cod: String,
    val message: String,
    val cnt: Int,
    @TypeConverters(ListDataConverter::class)
    val list: List<WeatherInfo>,
    @Embedded
    val city: City)
{
    @PrimaryKey(autoGenerate = false)
    var weatherid: Int = 0
}