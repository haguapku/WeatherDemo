package com.example.weatherdemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coord_table")
data class Coord constructor(
    val lat: Double,
    val lon: Double)
{
    @PrimaryKey(autoGenerate = false)
    var cid: Int = 0
}