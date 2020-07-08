package com.example.weatherdemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_table")
data class SearchHistoryItem(
    @PrimaryKey
    val name: String,
    var detail: String = "sun",
    var icon: String = "",
    var temp: Float = 0.0f,
    var checked: Boolean = false
)