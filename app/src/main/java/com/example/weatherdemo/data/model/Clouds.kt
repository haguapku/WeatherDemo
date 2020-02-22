package com.example.weatherdemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Clouds(val all: Int)
{
    @PrimaryKey(autoGenerate = false)
    var cloudsid: Int = 0
}