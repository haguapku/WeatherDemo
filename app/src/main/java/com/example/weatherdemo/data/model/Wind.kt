package com.example.weatherdemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Wind(val speed: Float, val deg: Int)
{
    @PrimaryKey(autoGenerate = false)
    var windid: Int = 0
}