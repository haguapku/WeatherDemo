package com.example.weatherdemo.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city_table")
data class City(
    val id: Long,
    val name: String,
    @Embedded(prefix = "coord_")
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long)
{
    @PrimaryKey(autoGenerate = false)
    var cityid: Int = 0
}