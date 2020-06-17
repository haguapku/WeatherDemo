package com.example.weatherdemo.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FeelsLike(
    @ColumnInfo(name = "feels_like_day")
    val day: Float,
    @ColumnInfo(name = "feels_like_night")
    val night: Float,
    @ColumnInfo(name = "feels_like_eve")
    val eve: Float,
    @ColumnInfo(name = "feels_like_morn")
    val morn: Float)
{
    @PrimaryKey(autoGenerate = false)
    var feelsLikeId: Int = 0
}