package com.example.weatherdemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sys(val pod: String)
{
    @PrimaryKey(autoGenerate = false)
    var sysid: Int = 0
}