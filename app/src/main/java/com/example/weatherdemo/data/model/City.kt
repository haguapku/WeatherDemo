package com.example.weatherdemo.data.model

import androidx.room.*

@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
@Entity(tableName = "city_table")
data class City(
    val id: Long,
    val name: String,
    @Embedded(prefix = "coord_")
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int)
{
    @PrimaryKey(autoGenerate = false)
    var cityid: Int = 0
}