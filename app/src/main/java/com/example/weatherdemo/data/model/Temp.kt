package com.example.weatherdemo.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Temp(
    @ColumnInfo(name = "temp_day")
    val day: Float,
    val min: Float,
    val max: Float,
    @ColumnInfo(name = "temp_night")
    val night: Float,
    @ColumnInfo(name = "temp_eve")
    val eve: Float,
    @ColumnInfo(name = "temp_morn")
    val morn: Float)
{
    @PrimaryKey(autoGenerate = false)
    var tempId: Int = 0
}